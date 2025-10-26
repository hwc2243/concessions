package com.concessions.local.ui.action;

public abstract class AbstractAction extends javax.swing.AbstractAction {

	public static final String OK_COMMAND = "OK";
	public static final String CANCEL_COMMAND = "CANCEL";
	
	public AbstractAction() {
		// TODO Auto-generated constructor stub
	}
	
	public AbstractAction (String name) {
		super(name);
	}

}
