package com.concessions.local.network.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.dto.JournalDTO;
import com.concessions.local.base.model.POSModel;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@ConditionalOnProperty(
	    value = "local.network.server", 
	    havingValue = "true", 
	    matchIfMissing = false
	)
public class JournalHandler extends AbstractDeviceHandler {

	public JournalHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return JOURNAL_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch (action) {
		case JOURNAL_GET_ACTION:
			return processJournalGet(payload);
		}
		throw new ServerException("Not implemented");
	}

	public JournalDTO processJournalGet (String payload) throws ServerException {
		try {
			SimpleDeviceRequestDTO request = mapper.readValue(payload, SimpleDeviceRequestDTO.class);
			validatePIN(request);
			validateDevice(request);
			JournalDTO response = model.getJournal();
			return response;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
}
