package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Order;

import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseOrderRestClient<T extends Order>
  extends AbstractRestClient
{
	@Value("${apiHostName:http://localhost:8080}")
	protected String hostPath;
	protected String apiPath = "/api/external/order";

	public BaseOrderRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T order = doPost(hostPath, apiPath, entity, new TypeToken<Order>() {}.getType());
			return CompletableFuture.completedFuture(order);
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
			T order = doGet(hostPath, apiPath + "/" + id, new TypeToken<Order>() {}.getType());
			return CompletableFuture.completedFuture(order);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> orders = doGet(hostPath, apiPath,
					new TypeToken<List<Order>>() {}.getType());
			
			return CompletableFuture.completedFuture(orders);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}