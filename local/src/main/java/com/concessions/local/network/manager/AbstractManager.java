package com.concessions.local.network.manager;

import com.concessions.local.network.dto.SimpleResponseDTO;

public abstract class AbstractManager {

	protected SimpleResponseDTO success;
	protected SimpleResponseDTO failure;
	
	public AbstractManager() {
		success = new SimpleResponseDTO();
		success.setMessage("Success");

		failure = new SimpleResponseDTO();
		failure.setMessage("Failure");
	}

	public abstract String getName ();
	
	public abstract Object process (String action, String payload) throws ServerException;
}
