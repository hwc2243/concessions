package com.concessions.local.bean;

public class ApplicationConfiguration extends AbstractConfiguration {

    // Constant for PropertyChangeListeners to track this specific property
    public static final String PROPERTY_APPLICATION_ROLE = "applicationRole";
    
    private ApplicationRole applicationRole = ApplicationRole.UNDECIDED;

    public ApplicationConfiguration() {
    }

    public ApplicationRole getApplicationRole() {
        return applicationRole;
    }

    public void setApplicationRole(ApplicationRole applicationRole) {
        ApplicationRole oldRole = this.applicationRole;
        this.applicationRole = applicationRole;
        
        // Standard AbstractBean/JavaBean notification
        firePropertyChange(PROPERTY_APPLICATION_ROLE, oldRole, applicationRole);
        updateConfigured();
    }

    @Override
    void updateConfigured () {
    	boolean oldConfigured = configured;
    	configured = applicationRole != ApplicationRole.UNDECIDED;
    	firePropertyChange(PROPERTY_CONFIGURED, oldConfigured, configured);
    }

	// Define the enum
    public enum ApplicationRole { 
        SERVER, CLIENT, UNDECIDED 
    }
}