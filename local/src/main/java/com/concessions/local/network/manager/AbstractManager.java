package com.concessions.local.network.manager;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.network.dto.AbstractPINRequestDTO;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.server.model.ServerApplicationModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

public abstract class AbstractManager {

	@Autowired
	protected ServerApplicationModel model;

	@Autowired
	protected ObjectMapper mapper;
	
	protected SimpleResponseDTO success;
	protected SimpleResponseDTO failure;
	
	public AbstractManager() {
		success = new SimpleResponseDTO();
		success.setMessage("Success");

		failure = new SimpleResponseDTO();
		failure.setMessage("Failure");
	}

	@PostConstruct
	private void register ()
	{
		ManagerRegistry.registerManager(this);
	}
	
	public abstract String getName ();
	
	public abstract Object process (String action, String payload) throws ServerException;
	
	protected void validatePIN (AbstractPINRequestDTO request) throws ServerException {
		if (!request.getPIN().equals(model.getPIN())) {
			throw new ServerException("PIN validation failed");
		}
	}
}
