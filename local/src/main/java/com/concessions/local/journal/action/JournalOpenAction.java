package com.concessions.local.journal.action;

import java.awt.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.action.AbstractAction;
import com.concessions.local.ui.controller.JournalController;

public class JournalOpenAction extends AbstractAction {

	@Autowired
	protected JournalController journalController;
	
	public JournalOpenAction() {
		super("Open");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		journalController.open();
	}
}
