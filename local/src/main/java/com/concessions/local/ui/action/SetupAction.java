package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.BackingStoreException;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.service.ApplicationConfigurationService;

@Component
public class SetupAction extends AbstractAction {

	@Autowired
	protected ApplicationConfiguration appConfig;
	
	@Autowired
	protected ApplicationConfigurationService appConfigService;
	
	public SetupAction() {
		super("Setup");
		putValue(Action.NAME, "Setup");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		setEnabled(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		appConfig.setLocationConfiguration(null);
		try {
			appConfigService.save();
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
		}
	}


}
