package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.security.TokenAuthService;
import com.concessions.local.ui.controller.DeviceCodeController;

@Component
public class LoginAction extends AbstractAction {
	
	@Autowired
	protected TokenAuthService authService;
	
	@Autowired
	protected DeviceCodeController deviceCodeController;

	public LoginAction() {
		super("Login");
		putValue(Action.NAME, "Login");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		deviceCodeController.execute();
	}

}
