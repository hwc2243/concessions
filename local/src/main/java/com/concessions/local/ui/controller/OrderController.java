package com.concessions.local.ui.controller;

import java.awt.Color;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.dto.MenuItemDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.dto.OrderItemDTO;
import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.network.dto.JournalMapper;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.DisabledLayerUI;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.model.OrderModel.OrderEntry;
import com.concessions.local.ui.view.OrderPanel;
import com.concessions.local.ui.view.OrderPanel.OrderActionListener;
import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;
import com.concessions.client.model.OrderItem;
import com.concessions.client.service.OrderItemService;
import com.concessions.client.service.OrderService;
import com.concessions.client.service.ServiceException;
import com.concessions.common.event.JournalListener;

import jakarta.annotation.PostConstruct;

@Component
public class OrderController implements OrderActionListener, JournalListener {

	@Autowired
	protected OrderAction orderAction;

	protected AbstractFrame applicationFrame;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected OrderItemService orderItemService;

	private JournalDTO journal = null;

	private OrderModel orderModel;

	private OrderPanel orderPanel;

	private List<OrderListener> listeners = new java.util.ArrayList<>();

	public OrderController(@Autowired AbstractFrame applicationFrame) {
		this.applicationFrame = applicationFrame;
	}

	@PostConstruct
	protected void initialize() {
	}

	public void addOrderListener(OrderListener listener) {
		listeners.add(listener);
	}

	public void removeOrderListener(OrderListener listener) {
		listeners.remove(listener);
	}

	protected void notifyOrderCreated(OrderDTO order) {
		listeners.stream().forEach(listener -> listener.onOrderCreated(order));
	}

	public void execute(MenuDTO menu, JournalDTO journal) {
		if (menu == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no menu loaded", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (journal == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no journal active", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.journal = journal;

		// the UI creation should happen once in initialize and should then support
		// changing the menuData
		orderModel = new OrderModel();
		orderModel.setMenu(menu);
		orderPanel = new OrderPanel(orderModel);
		orderPanel.addOrderActionListener(this);
		switch (journal.getStatus()) {
		case NEW:
			orderPanel.setInteractiveState(false, "Journal has not been started");
			break;
		case SUSPEND:
			orderPanel.setInteractiveState(false, "Journal is suspended");
			break;
		case CLOSE:
		case SYNC:
			orderPanel.setInteractiveState(false, "Journal is closed");
			break;
		default:
			break;
		}

		applicationFrame.setMainContent(orderPanel);
	}

	/**
	 * Recalculates the order total from the list model and updates the total label.
	 */
	private void updateTotal() {
		BigDecimal currentTotal = BigDecimal.ZERO;
		for (int i = 0; i < orderModel.getSize(); i++) {
			OrderEntry entry = orderModel.get(i);
			currentTotal = currentTotal.add(entry.menuItem().getPrice());
		}
		orderModel.setOrderTotal(currentTotal);
	}

	@Override
	public void onCheckout() {
		if (orderModel.getSize() == 0) {
			JOptionPane.showMessageDialog(applicationFrame, "There is no active order to checkout", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(applicationFrame, "Order Total: $"
				+ orderModel.getOrderTotal().setScale(2, RoundingMode.HALF_UP).toString() + "\nProcessing checkout...",
				"Checkout Complete", JOptionPane.INFORMATION_MESSAGE);

		OrderDTO order = new OrderDTO();
		order.setJournalId(journal.getId());
		order.setOrderTotal(orderModel.getOrderTotal());
		order.setMenuId(orderModel.getMenu().getId());
		order.setStartTs(LocalDateTime.now());

		List<OrderItemDTO> orderItems = orderModel.getOrderEntries().stream()
			    .map(orderEntry -> {
			        OrderItemDTO orderItem = new OrderItemDTO();
			        orderItem.setMenuItemId(orderEntry.menuItem().getId());
			        orderItem.setName(orderEntry.menuItem().getName());
			        orderItem.setPrice(orderEntry.menuItem().getPrice());
			        return orderItem;
			    })
			    .toList();

		order.setOrderItems(orderItems);

		notifyOrderCreated(order);

		orderModel.clear();
		updateTotal();
	}

	@Override
	public void onClear() {
		orderModel.clear();
		updateTotal();
	}

	@Override
	public void onItemAdded(MenuItemDTO item) {
		// Add the item to the order list model
		orderModel.add(new OrderEntry(item));
		updateTotal();
	}

	@Override
	public void onItemRemoved(int index) {
		orderModel.remove(index);
		updateTotal();
	}

	@Override
	public void journalClosed(JournalDTO journal) {
		if (orderPanel != null) {
			orderPanel.setInteractiveState(false, "Journal closed");
		}
		if (orderAction != null) {
			orderAction.setEnabled(false);
		}
	}

	@Override
	public void journalChanged(JournalDTO journal) {
	}

	@Override
	public void journalOpened(JournalDTO journal) {
		if (orderPanel != null) {
			orderPanel.setInteractiveState(true);
		}
		if (orderAction != null) {
			orderAction.setEnabled(true);
		}
		this.journal = journal;
	}

	@Override
	public void journalStarted(JournalDTO journal) {
		if (orderAction != null) {
			orderAction.setEnabled(false);
		}
	}

	@Override
	public void journalSuspended(JournalDTO journal) {
		if (orderPanel != null) {
			orderPanel.setInteractiveState(false, "Journal suspended");
		}
		if (orderAction != null) {
			orderAction.setEnabled(false);
		}
	}

	@Override
	public void journalSynced(JournalDTO journal) {
	}

	public interface OrderListener {
		public void onOrderCreated (OrderDTO order);
	}
}
