package com.concessions.local.network.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.ConfigurationRequestDTO;
import com.concessions.local.network.dto.ConfigurationResponseDTO;
import com.concessions.local.network.dto.DeviceRegistrationRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.annotation.PostConstruct;

@Component
public class ConfigurationManager extends AbstractManager {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	public static final String NAME = "CONFIG";
	public static final String LOCATION = "LOCATION";
	
	public ConfigurationManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch(action) {
		case LOCATION:
			return processLocation(payload);
		}
		throw new ServerException("Not implemented");
	}

	public ConfigurationResponseDTO processLocation (String payload) throws ServerException {
		try {
			ConfigurationRequestDTO request = mapper.readValue(payload, ConfigurationRequestDTO.class);
			validatePIN(request);
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
