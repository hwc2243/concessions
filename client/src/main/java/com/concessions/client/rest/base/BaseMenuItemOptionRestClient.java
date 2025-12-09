package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.MenuItemOption;

import com.google.gson.reflect.TypeToken;

public abstract class BaseMenuItemOptionRestClient<T extends MenuItemOption>
  extends MultitenantRestClient  
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/menuItemOption";

	public BaseMenuItemOptionRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T menuItemOption = doPost(hostPath, apiPath, entity, new TypeToken<MenuItemOption>() {}.getType());
			return CompletableFuture.completedFuture(menuItemOption);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T menuItemOption = doGet(hostPath, apiPath + "/" + id, new TypeToken<MenuItemOption>() {}.getType());
			return CompletableFuture.completedFuture(menuItemOption);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> menuItemOptions = doGet(hostPath, apiPath,
					new TypeToken<List<MenuItemOption>>() {}.getType());
			
			return CompletableFuture.completedFuture(menuItemOptions);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}