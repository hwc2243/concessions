package com.concessions.local.network.manager;

import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.MenuDTO;
import com.concessions.local.network.dto.SimpleDeviceRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class MenuManager extends AbstractManager {

	public static final String NAME = "MENU";
	
	public static final String GET = "GET";
	
	public MenuManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch(action) {
		case GET:
			return processGet(payload);
		}
		throw new ServerException("Not implemented");
	}
	
	protected MenuDTO processGet (String payload) throws ServerException {
		try {
			SimpleDeviceRequestDTO request = mapper.readValue(payload, SimpleDeviceRequestDTO.class);
			validatePIN(request);
			return model.getMenu();
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
}
