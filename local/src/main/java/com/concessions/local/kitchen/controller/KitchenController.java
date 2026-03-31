package com.concessions.local.kitchen.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.concessions.dto.OrderDTO;
import com.concessions.local.kitchen.model.KitchenModel;
import com.concessions.local.kitchen.ui.KitchenPanel;
import com.concessions.local.pos.processor.OrderProcessor;
import com.concessions.local.pos.processor.OrderProcessorDeprecated;
import com.concessions.local.server.ApplicationState;
import com.concessions.local.server.orchestrator.OrderException;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.server.orchestrator.OrderOrchestrator.OrderListener;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.view.OrderPanel;

import jakarta.annotation.PostConstruct;

public class KitchenController implements ApplicationState, OrderListener {

	private static final Logger logger = LoggerFactory.getLogger(KitchenController.class);

	@Autowired
	protected ApplicationFrame frame;

	@Autowired(required = false)
	private OrderOrchestrator orderOrchestrator;
	
	@Autowired
	protected OrderProcessor orderProcessor;

	private KitchenPanel kitchenPanel;

	protected KitchenModel kitchenModel;

	@PostConstruct
	protected void initialize() {
		kitchenModel = new KitchenModel();
		kitchenPanel = new KitchenPanel(this, kitchenModel);
		frame.addPanel(kitchenPanel, KitchenPanel.NAME);
		
		if (orderOrchestrator != null) {
			orderOrchestrator.addOrderListener(this);
			logger.info("KitchenController registered a local listener.");
		} else {
			logger.warn("OrderOrchestrator bean not found. Real-time order updates will be disabled.");
		}
	}

	@Override
	public boolean isComplete () {
		return true;
	}
	/**
	 * Handles the logic for completing an order.
	 * 
	 * @param order The order to be completed.
	 */
	public void completeOrder(OrderDTO order) {
		if (order != null) {
			try {
				orderProcessor.completeOrder(order);
				kitchenModel.removeOrder(order);
			} catch (OrderException ex) {
				JOptionPane.showMessageDialog(frame, "Failed to complete order", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void execute() {
		try {
			orderProcessor.getOrders().stream().forEach(order -> kitchenModel.addOrder(order));
		} catch (OrderException ex) {
			JOptionPane.showMessageDialog(frame, "Failed to retrieve orders - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
		
		frame.showPanel(KitchenPanel.NAME);
	}

	@Override
	public void orderCompleted(OrderDTO order) {
		logger.info("Received orderCompleted event for order: {}", order.getId());
		SwingUtilities.invokeLater(() -> kitchenModel.removeOrder(order));
	}

	@Override
	public void orderCreated(OrderDTO order) {
		logger.info("Received orderCreated event for order: {}", order.getId());
		SwingUtilities.invokeLater(() -> kitchenModel.addOrder(order));
	}
}
