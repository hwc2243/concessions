package com.concessions.local.network.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.OrderRequestDTO;
import com.concessions.local.network.dto.SimpleDeviceRequestDTO;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.server.controller.OrderSubmissionController;
import com.concessions.local.ui.controller.JournalController;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class OrderManager extends AbstractManager {

	public static final String NAME = "ORDER";
	
	public static final String JOURNAL_GET = "JOURNAL_GET";
	public static final String SUBMIT = "SUBMIT";
	
	@Autowired
	@Lazy
	protected OrderSubmissionController controller;
	
	@Autowired
	protected JournalController journalController;
	
	public OrderManager() {
	}

	@Override
	public String getName() {
		return this.NAME;
	}

	@Override
	public Object process (String action, String payload) throws ServerException {
		switch (action) {
		case JOURNAL_GET:
			return processJournalGet(payload);
		case SUBMIT:
			return processSubmit(payload);
		}
		throw new ServerException("Not implemented");
	}
	
	public JournalDTO processJournalGet (String payload) throws ServerException {
		try {
			SimpleDeviceRequestDTO request = mapper.readValue(payload, SimpleDeviceRequestDTO.class);
			validatePIN(request);
			JournalDTO response = model.getJournal();
			return response;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
	
	public SimpleResponseDTO processSubmit (String payload) throws ServerException {
		try {
			OrderRequestDTO request = mapper.readValue(payload, OrderRequestDTO.class);
			controller.onOrderCreated(request.getOrder());
			journalController.change(model.getJournal());
			return success;
			
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to submit order: " + ex.getMessage(), ex);
		}
	}
}
