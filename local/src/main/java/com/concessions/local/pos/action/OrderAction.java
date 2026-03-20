package com.concessions.local.pos.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.concessions.local.pos.controller.OrderController;
import com.concessions.local.ui.action.AbstractAction;

@Component
public class OrderAction extends AbstractAction {

	@Lazy
	@Autowired
	protected OrderController orderController;
	
	public OrderAction() {
		super("Order");
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		orderController.execute();
	}
}
