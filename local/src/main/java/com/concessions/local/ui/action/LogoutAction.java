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
import com.concessions.local.ui.model.ApplicationModel;

@Component
public class LogoutAction extends AbstractAction {
	
	@Autowired
	protected TokenAuthService authService;

	@Autowired
	protected ApplicationModel applicationModel;
	
	public LogoutAction() {
		super("Logout");
		putValue(Action.NAME, "Logout");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Logout action triggered");
		authService.clearTokenResponse();
		applicationModel.setTokenResponse(null);
	}
}
