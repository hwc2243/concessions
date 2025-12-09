package com.concessions.api.internal.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Journal;

import com.concessions.api.internal.base.BaseInternalJournalRest;

public interface BaseInternalJournalRest
{
	public ResponseEntity<Journal> createJournal (Journal journal);
	
    public ResponseEntity<Journal> deleteJournal(String id); 

	public ResponseEntity<Journal> getJournal (String id);
	
	public ResponseEntity<List<Journal>> listJournals ();
	
	public ResponseEntity<List<Journal>> searchJournals (
	);
	
    public ResponseEntity<Journal> updateJournal (String id, Journal journal);
}