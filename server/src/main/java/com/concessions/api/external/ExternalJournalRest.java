package com.concessions.api.external;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.concessions.api.external.base.BaseExternalJournalRest;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.model.Journal;
import com.concessions.model.Order;

public interface ExternalJournalRest extends BaseExternalJournalRest
{
	public ResponseEntity<Void> syncOrders(String journalId, List<Order> orders);
    public ResponseEntity<Journal> reconcileJournal(String journalId, JournalSummaryDTO summary);
}