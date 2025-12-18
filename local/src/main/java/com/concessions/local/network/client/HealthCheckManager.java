package com.concessions.local.network.client;

import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.network.manager.AbstractManager;
import com.concessions.local.network.manager.ServerException;

@Component
public class HealthCheckManager extends AbstractManager {

	public static final String NAME = "HEALTH";
	
	public static final String CHECK = "CHECK";
	
	public HealthCheckManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch (action) {
		case CHECK:
			return performHealthCheck(payload);
		}
		throw new ServerException("Not implemented");
	}

	public SimpleResponseDTO performHealthCheck (String payload) {
		return success;
	}
}
