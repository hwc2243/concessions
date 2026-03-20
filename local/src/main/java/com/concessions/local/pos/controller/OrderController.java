package com.concessions.local.pos.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.dto.MenuItemDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.dto.OrderItemDTO;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.network.Messenger;
import com.concessions.local.pos.processor.OrderProcessor;
import com.concessions.local.server.ApplicationState;
import com.concessions.local.server.orchestrator.OrderException;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.model.OrderModel.OrderEntry;
import com.concessions.local.ui.view.JournalPanel;
import com.concessions.local.ui.view.OrderPanel;
import com.concessions.local.ui.view.OrderPanel.OrderActionListener;
import com.concessions.client.service.OrderItemService;
import com.concessions.client.service.OrderService;
import com.concessions.common.event.JournalListener;
import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;

import jakarta.annotation.PostConstruct;

public class OrderController implements ApplicationState, OrderActionListener, JournalListener {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	protected ApplicationConfiguration appConfig;

	@Autowired
	protected Messenger messenger;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected OrderItemService orderItemService;

	protected ApplicationFrame applicationFrame;

	protected OrderProcessor orderSubmissionProcessor;

	private OrderModel orderModel;

	private OrderPanel orderPanel;

	public OrderController(@Autowired(required = false) ApplicationFrame applicationFrame,
			@Autowired OrderProcessor orderSubmissionProcessor) {
		this.applicationFrame = applicationFrame;
		this.orderSubmissionProcessor = orderSubmissionProcessor;
	}

	@PostConstruct
	protected void initialize() {
		orderModel = new OrderModel();
		orderPanel = new OrderPanel(orderModel);
		orderPanel.addOrderActionListener(this);

		applicationFrame.addPanel(orderPanel, OrderPanel.NAME);
	}

	@Override
	public boolean isComplete() {
		return (appConfig.getJournal() != null && appConfig.getMenu() != null);
	}

	public void execute() {
		if (appConfig.getMenu() == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no menu loaded", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (appConfig.getJournal() == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no journal active", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		orderModel.setMenu(appConfig.getMenu());
		// the UI creation should happen once in initialize and should then support
		// changing the menuData

		switch (appConfig.getJournal().getStatus()) {
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

		applicationFrame.showPanel(OrderPanel.NAME);
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
		order.setJournalId(appConfig.getJournal().getId());
		order.setOrderTotal(orderModel.getOrderTotal());
		order.setMenuId(orderModel.getMenu().getId());
		order.setStartTs(LocalDateTime.now());

		List<OrderItemDTO> orderItems = orderModel.getOrderEntries().stream().map(orderEntry -> {
			OrderItemDTO orderItem = new OrderItemDTO();
			orderItem.setMenuItemId(orderEntry.menuItem().getId());
			orderItem.setName(orderEntry.menuItem().getName());
			orderItem.setPrice(orderEntry.menuItem().getPrice());
			return orderItem;
		}).toList();

		order.setOrderItems(orderItems);

		OrderRequestDTO request = new OrderRequestDTO();
		request.setPIN(appConfig.getPin());
		request.setDeviceId(appConfig.getDeviceId());
		request.setOrder(order);
		try {
			messenger.sendRequest(NetworkConstants.ORDER_SERVICE, NetworkConstants.ORDER_SUBMIT_ACTION, request,
					SimpleResponseDTO.class);
			orderModel.clear();
			updateTotal();
		} catch (MessengerException ex) {
			logger.error("Failed to submit order - " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(applicationFrame, "Failed to submit order", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
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
	}

	@Override
	public void journalChanged(JournalDTO journal) {
	}

	@Override
	public void journalOpened(JournalDTO journal) {
		if (orderPanel != null) {
			orderPanel.setInteractiveState(true);
		}
	}

	@Override
	public void journalCreated(JournalDTO journal) {
	}

	@Override
	public void journalSuspended(JournalDTO journal) {
		if (orderPanel != null) {
			orderPanel.setInteractiveState(false, "Journal suspended");
		}
	}

	@Override
	public void journalSynced(JournalDTO journal) {
	}
}
