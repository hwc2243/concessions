package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.kitchen.controller.OrderDisplayController;
import com.concessions.local.server.model.ServerApplicationModel;

@Component
public class KitchenAction extends AbstractAction {

	@Autowired
	protected ServerApplicationModel model;
	
	@Autowired
	protected OrderDisplayController orderDisplayController;
	
	public KitchenAction() {
		super("Kitchen");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_K);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		//setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		orderDisplayController.execute();
	}

}
