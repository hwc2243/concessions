package com.concessions.local.bean;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.model.LocationConfiguration;
import com.nimbusds.oauth2.sdk.util.StringUtils;

public class ApplicationConfiguration extends AbstractConfiguration {

    // Constant for PropertyChangeListeners to track this specific property
    public static final String PROPERTY_APPLICATION_ROLE = "applicationRole";
    public static final String PROPERTY_DEVICE_ID = "deviceId";
    public static final String PROPERTY_DEVICE_TYPE = "deviceType";
	public static final String PROPERTY_PIN = "pin";
    public static final String PROPERTY_LOCATION_CONFIGURATION = "locationConfiguration";
    public static final String PROPERTY_ORGANIZATION_ID = "organizationId";
    public static final String PROPERTY_ORGANIZATION_NAME = "organizationName";
    public static final String PROPERTY_LOCATION_ID = "locationId";
    public static final String PROPERTY_LOCATION_NAME = "locationName";
    public static final String PROPERTY_MENU_ID = "menuId";
    public static final String PROPERTY_MENU_NAME = "menuName";
	public static final String PROPERTY_JOURNAL = "journal";
	public static final String PROPERTY_MENU = "menu";


    private ApplicationRole applicationRole = ApplicationRole.UNDECIDED;
    private String deviceId = null;
    // HWC TODO this shouldn't be hardcoded
    private DeviceTypeType deviceType = DeviceTypeType.SERVER;
    private LocationConfiguration locationConfiguration = null;
	private String pin = null;
	private JournalDTO journal = null;
	private MenuDTO menu = null;


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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		updateConfigured();
	}

	public DeviceTypeType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeType deviceType) {
		this.deviceType = deviceType;
		
		updateConfigured();
	}

	public LocationConfiguration getLocationConfiguration() {
		return locationConfiguration;
	}

	public void setLocationConfiguration(LocationConfiguration locationConfiguration) {
		LocationConfiguration oldLocationConfiguration = this.locationConfiguration;
		this.locationConfiguration = locationConfiguration;
		
		firePropertyChange(PROPERTY_LOCATION_CONFIGURATION, oldLocationConfiguration, locationConfiguration);
	}

	public boolean isLocationConfigured () {
		return (locationConfiguration != null &&
				locationConfiguration.getOrganizationId() != -1 &&
				StringUtils.isNotBlank(locationConfiguration.getOrganizationName()) &&
				locationConfiguration.getLocationId() != -1 &&
				StringUtils.isNotBlank(locationConfiguration.getLocationName()) &&
				locationConfiguration.getMenuId() != -1 &&
				StringUtils.isNotBlank(locationConfiguration.getMenuName()));
	}
	
	
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		String oldPin = this.pin;
		this.pin = pin;
		firePropertyChange(PROPERTY_PIN, oldPin, pin);
		
		updateConfigured();
	}
	
	public JournalDTO getJournal() {
		return journal;
	}

	public void setJournal(JournalDTO journal) {
		JournalDTO oldJournal = this.journal;
		this.journal = journal;
		
		firePropertyChange(PROPERTY_JOURNAL, oldJournal, journal);
	}

	public MenuDTO getMenu() {
		return menu;
	}

	public void setMenu(MenuDTO menu) {
		MenuDTO oldMenu = this.menu;
		this.menu = menu;
		
		firePropertyChange(PROPERTY_MENU, oldMenu, menu);
	}
	
	public void reset () {
		setApplicationRole(ApplicationRole.UNDECIDED);
		setDeviceId(null);
		setDeviceType(null);
		setLocationConfiguration(null);
		setPin(null);
		setJournal(null);
		setMenu(null);
	}
	
	@Override
    protected void updateConfigured () {
    	boolean oldConfigured = configured;
    	configured = applicationRole != ApplicationRole.UNDECIDED 
    			&& StringUtils.isNotBlank(deviceId)
    			&& deviceType != null
    			&& StringUtils.isNotBlank(pin);
    	
    	firePropertyChange(PROPERTY_CONFIGURED, oldConfigured, configured);
    }

	// Define the enum
    public enum ApplicationRole { 
        SERVER, CLIENT, UNDECIDED 
    }
}