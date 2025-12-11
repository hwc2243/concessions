package com.concessions.local.network.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.PINVerifyRequest;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.ui.model.ApplicationModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class PINManager extends AbstractManager {
	private static final Logger logger = LoggerFactory.getLogger(PINManager.class);

	public static final String NAME = "PIN";
	public static final String VERIFY_ACTION = "VERIFY";
	
	@Autowired
	protected ObjectMapper mapper;
	
	@Autowired
	protected ApplicationModel model;
	
	public PINManager() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	protected void register () {
		ManagerRegistry.registerManager(this);
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public Object process (String action, String payload) throws ServerException {
		
		logger.info("Processing {} : {}", action, payload);
		switch (action) {
		case VERIFY_ACTION:
			return processVerify(payload);
		default:
			throw new ServerException("Unknown action " + action);
		}
	}
	
	protected SimpleResponseDTO processVerify (String payload) throws ServerException {
		try {
			PINVerifyRequest request = mapper.readValue(payload, PINVerifyRequest.class);
			if (request.getPIN().equals(model.getPIN())) {
				return success;
			} else {
				throw new ServerException("PIN validation failed");
			}
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
}
