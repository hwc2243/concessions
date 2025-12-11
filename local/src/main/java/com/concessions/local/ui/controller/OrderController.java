package com.concessions.local.ui.controller;

import java.awt.Color;
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

import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.DisabledLayerUI;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.model.OrderModel.OrderEntry;
import com.concessions.local.ui.view.OrderPanel;
import com.concessions.local.ui.view.OrderPanel.OrderActionListener;
import com.concessions.client.model.CategoryType;
import com.concessions.client.model.Journal;
import com.concessions.client.model.Menu;
import com.concessions.client.model.MenuItem;
import com.concessions.client.model.Order;
import com.concessions.client.model.OrderItem;
import com.concessions.client.service.OrderItemService;
import com.concessions.client.service.OrderService;
import com.concessions.client.service.ServiceException;

import jakarta.annotation.PostConstruct;

@Component
public class OrderController 
	implements OrderActionListener, JournalListener {
	
	@Autowired
	protected OrderAction orderAction;
	
	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected OrderService orderService;
	
	@Autowired
	protected OrderItemService orderItemService;
	
	private DisabledLayerUI disableLayerUI;
	
	private JLayer<JPanel> disableLayer;
	
	private Journal journal = null;
	
	private OrderModel orderModel;
	
	private OrderPanel orderPanel;
	
	private List<OrderListener> listeners = new java.util.ArrayList<>();
	
	public OrderController() {
	}

	@PostConstruct
	protected void initialize ()
	{
	}
	
	public void addOrderListener (OrderListener listener) {
		listeners.add(listener);
	}

	public void removeOrderListener (OrderListener listener) {
		listeners.remove(listener);
	}

	protected void notifyOrderCreated (Order order) {
		listeners.stream().forEach(listener -> listener.onOrderCreated(order));
	}
	
	public void execute ()
	{
		Menu menu = applicationModel.getMenu();
		if (menu == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no menu loaded", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		journal = applicationModel.getJournal();
		if (journal == null) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to start order system, no journal active", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// the UI creation should happen once in initialize and should then support changing the menuData
		orderModel = new OrderModel();
		orderModel.setMenu(menu);
		orderPanel = new OrderPanel(orderModel);
		orderPanel.addOrderActionListener(this);

		disableLayerUI = new DisabledLayerUI();
        disableLayer = new JLayer<>(orderPanel, disableLayerUI);
        JPanel mainPane = new JPanel();
        mainPane.add(disableLayer);
		applicationFrame.setMainContent(mainPane);
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
        JOptionPane.showMessageDialog(applicationFrame, 
                "Order Total: $" + orderModel.getOrderTotal().setScale(2, RoundingMode.HALF_UP).toString() + "\nProcessing checkout...", 
                "Checkout Complete", 
                JOptionPane.INFORMATION_MESSAGE);

		
        Order order = orderService.newInstance(journal);
        order.setOrderTotal(orderModel.getOrderTotal());
        order.setMenuId(orderModel.getMenu().getId());
        order.setStartTs(LocalDateTime.now());

		orderModel.getOrderEntries().stream().forEach(orderEntry -> {
			OrderItem orderItem = orderItemService.newInstance();
			orderItem.setMenuItemId(orderEntry.menuItem().getId());
			orderItem.setName(orderEntry.menuItem().getName());
			orderItem.setPrice(orderEntry.menuItem().getPrice());
			order.addOrderItem(orderItem);
		});
		
        try {
        	Order persistedOrder = orderService.create(order);
        	notifyOrderCreated(persistedOrder);
        } catch (ServiceException ex) {
        	ex.printStackTrace();
        }
        
		orderModel.clear();
		updateTotal();
		
	}

	@Override
	public void onClear() {
		orderModel.clear();
		updateTotal();
	}

	@Override
	public void onItemAdded(MenuItem item) {
        // Add the item to the order list model
        orderModel.add(new OrderEntry(item));
        updateTotal();
	}

	@Override
	public void onItemRemoved(int index) {
		orderModel.remove(index);
		updateTotal();
	}
	
	
	public interface OrderListener {
		public void onOrderCreated (Order order);
	}


	@Override
	public void journalClosed(Journal journal) {
		// HWC this only get initialized the first time the view is activated
		if (disableLayer != null) {
			disableLayerUI.setMessage("Journal closed");
			disableLayer.setEnabled(false);
		}
		orderAction.setEnabled(false);
	}

	@Override
	public void journalOpened(Journal journal) {
		// HWC this should only get initialized the first time the view is activated
		if (disableLayer != null) {
			disableLayer.setEnabled(true);
		}
		orderAction.setEnabled(true);
	}

	@Override
	public void journalStarted(Journal journal) {
		orderAction.setEnabled(false);
	}

	@Override
	public void journalSuspended(Journal journal) {
		// HWC this only get initialized the first time the view is activated
		if (disableLayer != null) {
			disableLayerUI.setMessage("Journal suspended");
			disableLayer.setEnabled(false);
		}
		orderAction.setEnabled(false);
	}

	@Override
	public void journalSynced(Journal journal) {
		// HWC TODO can't think of anything to do at the moment
	}
}
