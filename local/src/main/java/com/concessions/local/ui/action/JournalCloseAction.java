package com.concessions.local.ui.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.controller.JournalController;

@Component
public class JournalCloseAction extends AbstractAction {

	@Autowired
	protected JournalController journalController;
	
	public JournalCloseAction() {
		super("Close");
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		journalController.close();
	}
}
