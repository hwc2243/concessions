package com.concessions.common.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import com.concessions.common.event.JournalNotifier;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.common.spring.NetworkClientCondition;
import com.concessions.dto.JournalDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Conditional(NetworkClientCondition.class)
public class JournalClientHandler extends AbstractHandler {
	
	protected JournalNotifier journalNotifier;

	public JournalClientHandler(@Autowired ObjectMapper mapper, @Autowired JournalNotifier journalNotifier) {
		super(mapper);
		this.journalNotifier = journalNotifier;
	}

	@Override
	public String getName() {
		return JOURNAL_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch (action) {
		case JOURNAL_CHANGE_ACTION:
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
