package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Menu;

import com.google.gson.reflect.TypeToken;

public abstract class BaseMenuRestClient<T extends Menu>
  extends MultitenantRestClient  
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/menu";

	public BaseMenuRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T menu = doPost(hostPath, apiPath, entity, new TypeToken<Menu>() {}.getType());
			return CompletableFuture.completedFuture(menu);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T menu = doGet(hostPath, apiPath + "/" + id, new TypeToken<Menu>() {}.getType());
			return CompletableFuture.completedFuture(menu);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> menus = doGet(hostPath, apiPath,
					new TypeToken<List<Menu>>() {}.getType());
			
			return CompletableFuture.completedFuture(menus);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}