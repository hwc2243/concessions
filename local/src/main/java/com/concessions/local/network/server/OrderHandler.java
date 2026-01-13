package com.concessions.local.network.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.concessions.common.network.AbstractHandler;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.local.server.controller.OrderSubmissionController;
import com.concessions.local.ui.controller.JournalController;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class OrderHandler extends AbstractDeviceHandler {
	
	@Autowired
	@Lazy
	protected OrderSubmissionController controller;
	
	@Autowired
	protected JournalController journalController;
	
	public OrderHandler() {
	}

	@Override
	public String getName() {
		return ORDER_SERVICE;
	}

	@Override
	public Object process (String action, String payload) throws ServerException {
		switch (action) {
		case ORDER_SUBMIT_ACTION:
			return processSubmit(payload);
		}
		throw new ServerException("Not implemented");
	}
	
	public SimpleResponseDTO processSubmit (String payload) throws ServerException {
		try {
			OrderRequestDTO request = mapper.readValue(payload, OrderRequestDTO.class);
			validatePIN(request);
			validateDevice(request);
			controller.onOrderCreated(request.getOrder());
			journalController.change(model.getJournal());
			return success;
			
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to submit order: " + ex.getMessage(), ex);
		}
	}
}
