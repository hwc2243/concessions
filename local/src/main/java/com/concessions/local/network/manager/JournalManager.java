package com.concessions.local.network.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.StatusType;
import com.concessions.local.base.model.POSModel;
import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.SimpleDeviceRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class JournalManager extends AbstractManager {

	public static final String NAME = "JOURNAL";
	
	public static final String JOURNAL_GET = "GET";

	public JournalManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch (action) {
		case JOURNAL_GET:
			return processJournalGet(payload);
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
}
