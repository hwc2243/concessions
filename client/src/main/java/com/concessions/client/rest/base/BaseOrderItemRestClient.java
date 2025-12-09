package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.OrderItem;

import com.google.gson.reflect.TypeToken;

public abstract class BaseOrderItemRestClient<T extends OrderItem>
  extends AbstractRestClient
{
	protected String hostPath = "http://localhost:8080";
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