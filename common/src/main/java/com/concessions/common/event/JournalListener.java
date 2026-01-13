package com.concessions.common.event;

import com.concessions.dto.JournalDTO;

public interface JournalListener {
	void journalClosed(JournalDTO journal);
	
	void journalChanged(JournalDTO journal);

	void journalOpened(JournalDTO journal);

	void journalStarted(JournalDTO journal);

	void journalSuspended(JournalDTO journal);
	
	void journalSynced(JournalDTO journal);
}