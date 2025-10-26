package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.controller.SetupController;

@Component
public class SetupAction extends AbstractAction {

	@Autowired
	protected SetupController setupController;

	public SetupAction() {
		super("Setup");
		putValue(Action.NAME, "Setup");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		setEnabled(false);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Login action triggered");
		setupController.execute();
	}


}
