package com.concessions.local.ui.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.service.JournalService;
import com.concessions.local.ui.controller.JournalController;

@Component
public class JournalViewAction extends AbstractAction {

	@Autowired
	protected JournalController journalController;
	
	public JournalViewAction() {
		super("View");
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		journalController.view();
	}
}
