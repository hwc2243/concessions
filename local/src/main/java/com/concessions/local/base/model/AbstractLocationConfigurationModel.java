package com.concessions.local.base.model;

import com.concessions.local.model.LocationConfiguration;

public class AbstractLocationConfigurationModel extends AbstractModel {

	public static final String LOCATION_CONFIGURATION = "locationConfiguration";
	
	protected LocationConfiguration locationConfiguration;
	
	public AbstractLocationConfigurationModel() {
		// TODO Auto-generated constructor stub
	}

	public LocationConfiguration getLocationConfiguration() {
		return locationConfiguration;
	}

	public void setLocationConfiguration (LocationConfiguration locationConfiguration) {
		LocationConfiguration oldLocationConfiguration = this.locationConfiguration;
		this.locationConfiguration = locationConfiguration;
		this.firePropertyChange(LOCATION_CONFIGURATION, oldLocationConfiguration, locationConfiguration);
	}
}
