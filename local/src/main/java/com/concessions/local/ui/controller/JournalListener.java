package com.concessions.local.ui.controller;

import com.concessions.client.model.Journal;

public interface JournalListener {
	void journalClosed(Journal journal);

	void journalOpened(Journal journal);

	void journalStarted(Journal journal);

	void journalSuspended(Journal journal);
	
	void journalSynced(Journal journal);
}
