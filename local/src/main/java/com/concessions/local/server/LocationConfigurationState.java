package com.concessions.local.server;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.ConfigurationResponseDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.LocationConfiguration;
import com.concessions.local.network.Messenger;

@Component
public class LocationConfigurationState implements ApplicationState {

	@Autowired
	protected ApplicationConfiguration appConfig;
	
	@Autowired
	protected Messenger messenger;
	
	@Override
	public boolean isComplete() {
		return appConfig.isLocationConfigured();
	}

	@Override
	public void execute() {
		SimpleDeviceRequestDTO request = new SimpleDeviceRequestDTO();
		request.setPIN(appConfig.getPin());
		request.setDeviceId(appConfig.getDeviceId());
		
		ConfigurationResponseDTO response = null;
		try {
			response = messenger.sendRequest(NetworkConstants.CONFIGURATION_SERVICE, NetworkConstants.CONFIGURATION_LOCATION_ACTION, request, ConfigurationResponseDTO.class);
			LocationConfiguration locationConfiguration = new LocationConfiguration();
			locationConfiguration.setOrganizationId(response.getOrganizationId());
			locationConfiguration.setOrganizationName(response.getOrganizationName());
			locationConfiguration.setLocationId(response.getLocationId());
			locationConfiguration.setLocationName(response.getLocationName());
			locationConfiguration.setMenuId(response.getMenuId());
			locationConfiguration.setMenuName(response.getMenuName());
			appConfig.setLocationConfiguration(locationConfiguration);
		} catch (MessengerException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve location configuration - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
