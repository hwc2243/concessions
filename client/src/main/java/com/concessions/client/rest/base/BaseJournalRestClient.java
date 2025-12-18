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

/*
    public CompletableFuture<T> get (long id) {
    // 1. supplyAsync moves the execution to a worker thread (usually ForkJoinPool.commonPool())
    return CompletableFuture.supplyAsync(() -> {
        try {
            // 2. This synchronous call (doGet) now executes on a background thread.
            // Note: We use the target type here, which is Organization in your case
            return doGet(hostPath, apiPath + "/" + id, new TypeToken<Organization>() {}.getType());
        } catch (IOException | InterruptedException e) {
            // 3. If an exception occurs, it must be thrown inside the supplier
            // This causes the CompletableFuture to complete exceptionally.
            throw new RuntimeException(e);
        }
    });
    // The .exceptionally() or .handle() method is often used after this 
    // to process the exception on the main thread if needed.
    }
*/	
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