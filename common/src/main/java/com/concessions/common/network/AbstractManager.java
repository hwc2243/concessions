package com.concessions.common.network;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.dto.SimpleResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

public abstract class AbstractManager implements NetworkConstants {

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

	public AbstractManager (ObjectMapper mapper) {
		this();
		
		this.mapper = mapper;
	}
	
	@PostConstruct
	public void register ()
	{
		ManagerRegistry.registerManager(this);
	}
	
	public abstract String getName ();
	
	public abstract Object process (String action, String payload) throws NetworkException;
}