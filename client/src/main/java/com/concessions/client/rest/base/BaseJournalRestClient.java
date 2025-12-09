package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Journal;

import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseJournalRestClient<T extends Journal>
  extends MultitenantRestClient  
{
	@Value("${apiHostName:http://localhost:8080}")
	protected String hostPath;
	protected String apiPath = "/api/external/journal";

	public BaseJournalRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T journal = doPost(hostPath, apiPath, entity, new TypeToken<Journal>() {}.getType());
			return CompletableFuture.completedFuture(journal);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T journal = doGet(hostPath, apiPath + "/" + id, new TypeToken<Journal>() {}.getType());
			return CompletableFuture.completedFuture(journal);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> journals = doGet(hostPath, apiPath,
					new TypeToken<List<Journal>>() {}.getType());
			
			return CompletableFuture.completedFuture(journals);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}