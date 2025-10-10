package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.service.TokenAuthService;

@Component
public class LogoutAction extends AbstractAction {
	
	@Autowired
	protected TokenAuthService authService;

	public LogoutAction() {
		super("Logout");
		System.out.println("LogoutAction initialized");
		putValue(Action.NAME, "Logout");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Logout action triggered");
		authService.clearTokenResponse();
		setEnabled(false);
	}

}
