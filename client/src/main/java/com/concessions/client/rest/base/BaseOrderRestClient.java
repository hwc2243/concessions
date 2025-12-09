package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Order;

import com.google.gson.reflect.TypeToken;

public abstract class BaseOrderRestClient<T extends Order>
  extends AbstractRestClient
{
	protected String hostPath = "http://localhost:8080";
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