package com.concessions.local.server.model;

import com.concessions.local.bean.AbstractBean;

public class ApplicationModel extends AbstractBean {
	public static final String PROPERTY_CONNECTED = "connected";
    public static final String PROPERTY_STATUS = "status";

    private boolean connected = false;
    private String status = "Initializing...";

    
    public boolean isConnected () {
		return connected;
	}

	public void setConnected (boolean connected) {
		boolean oldConnected = connected;
		this.connected = connected;
		firePropertyChange(PROPERTY_CONNECTED, oldConnected, connected);
	}

	public String getStatus () {
		return status;
	}

	public void setStatus (String status) {
		String oldStatus = this.status;
		this.status = status;
		firePropertyChange(PROPERTY_STATUS, oldStatus, status);
	}
}
