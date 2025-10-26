package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.controller.OrderController;

@Component
public class OrderAction extends AbstractAction {

	@Autowired
	protected OrderController orderController;
	
	public OrderAction() {
		super("Order");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		orderController.execute();
	}

}
