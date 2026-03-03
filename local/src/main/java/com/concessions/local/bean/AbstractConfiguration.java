package com.concessions.local.bean;

public abstract class AbstractConfiguration extends AbstractBean {
    public static final String PROPERTY_CONFIGURED = "configured";

    protected boolean configured = false;
    
    public boolean isConfigured () {
    	return this.configured;
    }

    abstract void updateConfigured ();

}
