package com.concessions.local.ui.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.controller.JournalController;

@Component
public class JournalSuspendAction extends AbstractAction {

	@Autowired
	protected JournalController journalController;
	
	public JournalSuspendAction() {
		super("Suspend");
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		journalController.suspend();
	}
}
