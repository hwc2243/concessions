package com.concessions.local.ui.model;

import org.springframework.stereotype.Component;

@Component
public class ApplicationModel extends AbstractModel {

	private String statusMessage;
	
	public ApplicationModel() {
		// TODO Auto-generated constructor stub
	}

	public String getStatusMessage() {
		return statusMessage;
	}
	
	public void setStatusMessage(String statusMessage) {
		String oldMessage = this.statusMessage;
		this.statusMessage = statusMessage;
		firePropertyChange("statusMessage", oldMessage, statusMessage);
	}
}
