package com.concessions.api.external.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Journal;

import com.concessions.api.external.base.BaseExternalJournalRest;

public interface BaseExternalJournalRest
{
	public ResponseEntity<Journal> createJournal (Journal journal);
	
    public ResponseEntity<Journal> deleteJournal(String id); 

	public ResponseEntity<Journal> getJournal (String id);
	
	public ResponseEntity<List<Journal>> listJournals ();
	
	public ResponseEntity<List<Journal>> searchJournals (
	);
	
    public ResponseEntity<Journal> updateJournal (String id, Journal journal);
}