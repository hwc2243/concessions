package com.concessions.common.network;

import org.springframework.stereotype.Component;

import com.concessions.common.network.dto.SimpleResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HealthCheckManager extends AbstractManager {

	
	public HealthCheckManager() {
		// TODO Auto-generated constructor stub
	}
	
	public HealthCheckManager (ObjectMapper mapper) {
		super(mapper);
	}

	@Override
	public String getName() {
		return HEALTH_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws NetworkException {
		switch (action) {
		case HEALTH_CHECK_ACTION:
			return performHealthCheck(payload);
		}
		throw new NetworkException("Not implemented");
	}

	public SimpleResponseDTO performHealthCheck (String payload) {
		return success;
	}
}
