package com.concessions.local.bean;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.nimbusds.oauth2.sdk.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

public class ApplicationConfiguration extends AbstractConfiguration {

    // Constant for PropertyChangeListeners to track this specific property
    public static final String PROPERTY_APPLICATION_ROLE = "applicationRole";
    public static final String PROPERTY_DEVICE_ID = "deviceId";
    public static final String PROPERTY_DEVICE_NUMBER = "deviceNumber";
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

	@Getter
    private ApplicationRole applicationRole = ApplicationRole.UNDECIDED;
	
	@Getter
    private String deviceId = null;
	
	@Getter
	private String deviceNumber = null;
	
    // HWC TODO this shouldn't be hardcoded
	@Getter
    private DeviceTypeType deviceType = DeviceTypeType.SERVER;
	
	@Getter
	@Setter
	private long organizationId;
	
	@Getter
    private LocationConfiguration locationConfiguration = null;
	
	@Getter
	private String pin = null;
	
	@Getter
	private JournalDTO journal = null;
	
	@Getter
	private MenuDTO menu = null;


    public ApplicationConfiguration() {
    }

    public void setApplicationRole(ApplicationRole applicationRole) {
        ApplicationRole oldRole = this.applicationRole;
        this.applicationRole = applicationRole;
        
        // Standard AbstractBean/JavaBean notification
        firePropertyChange(PROPERTY_APPLICATION_ROLE, oldRole, applicationRole);
        updateConfigured();
    }

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		updateConfigured();
	}
	
	public void setDeviceNumber (String deviceNumber) {
		this.deviceNumber = deviceNumber;
		updateConfigured();
	}

	public void setDeviceType(DeviceTypeType deviceType) {
		this.deviceType = deviceType;
		
		updateConfigured();
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
	
	public void setPin(String pin) {
		String oldPin = this.pin;
		this.pin = pin;
		firePropertyChange(PROPERTY_PIN, oldPin, pin);
		
		updateConfigured();
	}
	
	public void setJournal(JournalDTO journal) {
		JournalDTO oldJournal = this.journal;
		this.journal = journal;
		
		firePropertyChange(PROPERTY_JOURNAL, oldJournal, journal);
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