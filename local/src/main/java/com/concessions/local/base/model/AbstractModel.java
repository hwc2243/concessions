package com.concessions.local.base.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel {

	public static final String STATUS_MESSAGE = "statusMessage";
	
	protected String statusMessage = "";
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public AbstractModel() {
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage (String statusMessage) {
		String oldStatusMessage = this.statusMessage;
		this.statusMessage = statusMessage;
		this.firePropertyChange(STATUS_MESSAGE, oldStatusMessage, statusMessage);
	}
    
    
}
