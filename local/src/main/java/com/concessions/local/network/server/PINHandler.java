package com.concessions.local.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.concessions.common.network.AbstractHandler;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.PINVerifyRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.annotation.PostConstruct;

@Component
public class PINHandler extends AbstractPINHandler {
	private static final Logger logger = LoggerFactory.getLogger(PINHandler.class);

	public PINHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getName() {
		return PIN_SERVICE;
	}

	@Override
	public Object process (String action, String payload) throws ServerException {
		
		logger.info("Processing {} : {}", action, payload);
		switch (action) {
		case PIN_VERIFY_ACTION:
			return processVerify(payload);
		default:
			throw new ServerException("Unknown action " + action);
		}
	}
	
	protected SimpleResponseDTO processVerify (String payload) throws ServerException {
		try {
			PINVerifyRequestDTO request = mapper.readValue(payload, PINVerifyRequestDTO.class);
			validatePIN(request);
			return success;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
}
