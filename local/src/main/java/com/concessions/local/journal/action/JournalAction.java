package com.concessions.local.journal.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.concessions.client.service.JournalService;
import com.concessions.local.ui.action.AbstractAction;
import com.concessions.local.ui.controller.JournalController;

@Component
public class JournalAction extends AbstractAction {

	@Lazy
	@Autowired
	protected JournalController journalController;
	
	public JournalAction() {
		super("Journal");
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		journalController.view();
	}
}
