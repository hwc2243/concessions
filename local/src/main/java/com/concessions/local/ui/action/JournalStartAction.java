package com.concessions.local.ui.action;

import java.awt.event.ActionEvent;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.service.JournalService;
import com.concessions.local.ui.controller.JournalController;

@Component
public class JournalStartAction extends AbstractAction {

	@Autowired
	protected JournalController journalController;
	
	public JournalStartAction() {
		super("Start");
		setEnabled(false);
	}


	@Override
	public void actionPerformed (ActionEvent e) {
		journalController.start();
	}
}
