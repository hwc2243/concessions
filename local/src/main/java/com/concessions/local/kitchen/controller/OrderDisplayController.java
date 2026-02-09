package com.concessions.local.kitchen.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.concessions.dto.OrderDTO;
import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.kitchen.model.OrderDisplayModel;
import com.concessions.local.kitchen.ui.OrderDisplay;
import com.concessions.local.pos.processor.OrderProcessor;
import com.concessions.local.server.orchestrator.OrderException;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.server.orchestrator.OrderOrchestrator.OrderListener;

import jakarta.annotation.PostConstruct;

@Component
public class OrderDisplayController implements OrderListener {

	private static final Logger logger = LoggerFactory.getLogger(OrderDisplayController.class);

	@Autowired
	@Qualifier("applicationFrame")
	protected AbstractFrame frame;

	@Autowired
	protected OrderProcessor orderProcessor;

	@Autowired
	private OrderDisplay orderDisplayComponent;

	@Autowired
	protected OrderDisplayModel orderDisplayModel;

	@Autowired(required = false)
	private OrderOrchestrator orderOrchestrator;

	@PostConstruct
	protected void initialize() {
		if (orderOrchestrator != null) {
			orderOrchestrator.addOrderListener(this);
			logger.info("OrderDisplayController registered a local listener.");
		} else {
			logger.warn("OrderOrchestrator bean not found. Real-time order updates will be disabled.");
		}
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
				orderDisplayModel.removeOrder(order);
			} catch (OrderException ex) {
				JOptionPane.showMessageDialog(frame, "Failed to complete order", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void execute() {
		frame.setMainContent(orderDisplayComponent);
		try {
			orderProcessor.getOrders().stream().forEach(order -> orderDisplayModel.addOrder(order));
		} catch (OrderException ex) {
			JOptionPane.showMessageDialog(frame, "Failed to retrieve orders - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void orderCompleted(OrderDTO order) {
		logger.info("Received orderCompleted event for order: {}", order.getId());
		SwingUtilities.invokeLater(() -> orderDisplayModel.removeOrder(order));
	}

	@Override
	public void orderCreated(OrderDTO order) {
		logger.info("Received orderCreated event for order: {}", order.getId());
		SwingUtilities.invokeLater(() -> orderDisplayModel.addOrder(order));
	}
}
