package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.MenuItem;

import com.google.gson.reflect.TypeToken;

public abstract class BaseMenuItemRestClient<T extends MenuItem>
  extends MultitenantRestClient  
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/menuItem";

	public BaseMenuItemRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T menuItem = doPost(hostPath, apiPath, entity, new TypeToken<MenuItem>() {}.getType());
			return CompletableFuture.completedFuture(menuItem);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T menuItem = doGet(hostPath, apiPath + "/" + id, new TypeToken<MenuItem>() {}.getType());
			return CompletableFuture.completedFuture(menuItem);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> menuItems = doGet(hostPath, apiPath,
					new TypeToken<List<MenuItem>>() {}.getType());
			
			return CompletableFuture.completedFuture(menuItems);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}