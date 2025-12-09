package com.concessions.client.rest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;
import com.concessions.client.rest.base.BaseJournalRestClient;
import com.concessions.common.dto.JournalSummaryDTO;
import com.google.gson.reflect.TypeToken;

import java.util.List;

@Service
public class JournalRestClient extends BaseJournalRestClient<Journal>
{
	public CompletableFuture<Journal> syncOrders (Journal journal, List<Order> orders) {
		try {
			
			Journal result = doPatch(hostPath, apiPath + "/" + journal.getId() + "/syncOrders", orders, new TypeToken<Journal>() {}.getType());
			return CompletableFuture.completedFuture(result);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<Journal> reconcile (Journal journal, JournalSummaryDTO summary) {
		try {
			Journal result = doPost(hostPath, apiPath + "/" + journal.getId() + "/reconcile", summary, new TypeToken<Journal>() {}.getType());
			return CompletableFuture.completedFuture(result);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}