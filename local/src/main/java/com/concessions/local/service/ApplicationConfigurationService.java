package com.concessions.local.service;

import java.util.prefs.BackingStoreException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ApplicationConfiguration.ApplicationRole;
import com.concessions.local.bean.LocationConfiguration;
import com.concessions.local.dto.DeviceTypeType;
 
import jakarta.annotation.PostConstruct;

@Service
public class ApplicationConfigurationService {

	protected PreferenceService preferenceService;

	private static ApplicationConfiguration applicationConfiguration;

	public ApplicationConfigurationService(@Autowired PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
		this.applicationConfiguration = new ApplicationConfiguration();
	}

	@PostConstruct
	public void initialize() {
		String applicationRole = preferenceService.get(ApplicationConfiguration.PROPERTY_APPLICATION_ROLE);
		if (StringUtils.isNotBlank(applicationRole)) {
			applicationConfiguration.setApplicationRole(ApplicationRole.valueOf(applicationRole));
		} else {
			applicationConfiguration.setApplicationRole(ApplicationRole.UNDECIDED);
		}

		String deviceId = preferenceService.get(ApplicationConfiguration.PROPERTY_DEVICE_ID);
		if (StringUtils.isNotBlank(deviceId)) {
			applicationConfiguration.setDeviceId(deviceId);
		}
		
		String deviceTypeType = preferenceService.get(ApplicationConfiguration.PROPERTY_DEVICE_TYPE);
		if (StringUtils.isNotBlank(deviceTypeType)) {
			applicationConfiguration.setDeviceType(DeviceTypeType.valueOf(deviceTypeType));
		}

		String pin = preferenceService.get(ApplicationConfiguration.PROPERTY_PIN);
		if (StringUtils.isNotBlank(pin)) {
			applicationConfiguration.setPin(pin);
		}
		
		LocationConfiguration locationConfiguration = new LocationConfiguration();
		locationConfiguration
				.setOrganizationId(preferenceService.getLong(ApplicationConfiguration.PROPERTY_ORGANIZATION_ID));
		locationConfiguration
				.setOrganizationName(preferenceService.get(ApplicationConfiguration.PROPERTY_ORGANIZATION_NAME));
		locationConfiguration.setLocationId(preferenceService.getLong(ApplicationConfiguration.PROPERTY_LOCATION_ID));
		locationConfiguration.setLocationName(preferenceService.get(ApplicationConfiguration.PROPERTY_LOCATION_NAME));
		locationConfiguration.setMenuId(preferenceService.getLong(ApplicationConfiguration.PROPERTY_MENU_ID));
		locationConfiguration.setMenuName(preferenceService.get(ApplicationConfiguration.PROPERTY_MENU_NAME));
		applicationConfiguration.setLocationConfiguration(locationConfiguration);
		applicationConfiguration.setOrganizationId(locationConfiguration.getOrganizationId());

	}

	public ApplicationConfiguration get() {
		return applicationConfiguration;
	}

	public void reset() throws BackingStoreException {
		preferenceService.clear(ApplicationConfiguration.PROPERTY_APPLICATION_ROLE);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_PIN);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_ORGANIZATION_ID);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_ORGANIZATION_NAME);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_LOCATION_ID);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_LOCATION_NAME);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_MENU_ID);
		preferenceService.clear(ApplicationConfiguration.PROPERTY_MENU_NAME);
		applicationConfiguration.reset();
	}

	public void save() throws BackingStoreException {
		if (applicationConfiguration.isConfigured()) {
			preferenceService.save(ApplicationConfiguration.PROPERTY_APPLICATION_ROLE,
					applicationConfiguration.getApplicationRole().toString());
		}

		if (StringUtils.isBlank(applicationConfiguration.getDeviceId())) {
			preferenceService.clear(ApplicationConfiguration.PROPERTY_DEVICE_ID);
		} else {
			preferenceService.save(ApplicationConfiguration.PROPERTY_DEVICE_ID, applicationConfiguration.getDeviceId());
		}
		
		if (applicationConfiguration.getDeviceType() == null) {
			preferenceService.clear(ApplicationConfiguration.PROPERTY_DEVICE_TYPE);
		} else {
			preferenceService.save(ApplicationConfiguration.PROPERTY_DEVICE_TYPE, applicationConfiguration.getDeviceType().getName());
		}

		if (StringUtils.isBlank(applicationConfiguration.getPin())) {
			preferenceService.clear(ApplicationConfiguration.PROPERTY_PIN);
		} else {
			preferenceService.save(ApplicationConfiguration.PROPERTY_PIN, applicationConfiguration.getPin());
		}
		
		if (applicationConfiguration.getLocationConfiguration() != null) {
			LocationConfiguration locationConfiguration = applicationConfiguration.getLocationConfiguration();
			if (locationConfiguration.getOrganizationId() != -1) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_ORGANIZATION_ID,
						locationConfiguration.getOrganizationId());
			}
			if (StringUtils.isNotBlank(locationConfiguration.getOrganizationName())) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_ORGANIZATION_NAME,
						locationConfiguration.getOrganizationName());
			}

			if (locationConfiguration.getLocationId() != -1) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_LOCATION_ID,
						locationConfiguration.getLocationId());
			}
			if (StringUtils.isNotBlank(locationConfiguration.getLocationName())) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_LOCATION_NAME,
						locationConfiguration.getLocationName());
			}

			if (locationConfiguration.getMenuId() != -1) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_MENU_ID, locationConfiguration.getMenuId());
			}
			if (StringUtils.isNotBlank(locationConfiguration.getMenuName())) {
				preferenceService.save(ApplicationConfiguration.PROPERTY_MENU_NAME,
						locationConfiguration.getMenuName());
			}
		}
	}
}
