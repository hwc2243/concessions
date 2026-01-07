package com.concessions.local.network.server;

import org.springframework.stereotype.Component;

import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.dto.MenuDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class MenuManager extends AbstractPINManager {

	public MenuManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return MENU_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch(action) {
		case MENU_GET_ACTION:
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
