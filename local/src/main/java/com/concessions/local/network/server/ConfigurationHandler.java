package com.concessions.local.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.concessions.common.network.AbstractHandler;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.ConfigurationResponseDTO;
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.annotation.PostConstruct;

@Component
public class ConfigurationHandler extends AbstractDeviceHandler {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationHandler.class);

	public ConfigurationHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return CONFIGURATION_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch(action) {
		case CONFIGURATION_LOCATION_ACTION:
			return processLocation(payload);
		}
		throw new ServerException("Not implemented");
	}

	public ConfigurationResponseDTO processLocation (String payload) throws ServerException {
		try {
			SimpleDeviceRequestDTO request = mapper.readValue(payload, SimpleDeviceRequestDTO.class);
			validatePIN(request);
			validateDevice(request);
			ConfigurationResponseDTO response = new ConfigurationResponseDTO();
			if (model.getLocationConfiguration() == null) {
				throw new ServerException("Location has not been configurated yet");
			}
			response.setOrganizationName(model.getLocationConfiguration().getOrganizationName());
			response.setLocationName(model.getLocationConfiguration().getLocationName());
			response.setMenuName(model.getLocationConfiguration().getMenuName());
			return response;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);

		}
	}
}
