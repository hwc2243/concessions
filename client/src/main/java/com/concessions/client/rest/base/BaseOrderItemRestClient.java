package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.OrderItem;

import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseOrderItemRestClient<T extends OrderItem>
  extends AbstractRestClient
{
	@Value("${apiHostName:http://localhost:8080}")
	protected String hostPath;
	protected String apiPath = "/api/external/orderItem";

	public BaseOrderItemRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T orderItem = doPost(hostPath, apiPath, entity, new TypeToken<OrderItem>() {}.getType());
			return CompletableFuture.completedFuture(orderItem);
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
			T orderItem = doGet(hostPath, apiPath + "/" + id, new TypeToken<OrderItem>() {}.getType());
			return CompletableFuture.completedFuture(orderItem);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> orderItems = doGet(hostPath, apiPath,
					new TypeToken<List<OrderItem>>() {}.getType());
			
			return CompletableFuture.completedFuture(orderItems);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}