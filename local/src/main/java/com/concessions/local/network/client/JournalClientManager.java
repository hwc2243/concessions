package com.concessions.local.network.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.network.manager.AbstractManager;
import com.concessions.local.network.manager.ServerException;
import com.concessions.local.ui.JournalNotifier;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class JournalClientManager extends AbstractManager {
	
	@Autowired
	protected JournalNotifier journalNotifier;

	public static final String NAME = "JOURNAL";
	
	public static final String CHANGE = "CHANGE";
	
	public JournalClientManager() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch (action) {
		case CHANGE:
			return processJournalChange(payload);
		}
		throw new ServerException("Not implemented");
	}
	
	public SimpleResponseDTO processJournalChange (String payload) throws ServerException {
		try {
			JournalDTO journal = mapper.readValue(payload, JournalDTO.class);
			switch (journal.getStatus()) {
			case OPEN:
				journalNotifier.notifyJournalOpened(journal);
				break;
			case SUSPEND:
				journalNotifier.notifyJournalSuspended(journal);
				break;
			case CLOSE:
				journalNotifier.notifyJournalClosed(journal);
				break;
			}
			return success;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		}
	}
}
