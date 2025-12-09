package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.User;

import com.google.gson.reflect.TypeToken;

public abstract class BaseUserRestClient<T extends User>
  extends AbstractRestClient
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/user";

	public BaseUserRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T user = doPost(hostPath, apiPath, entity, new TypeToken<User>() {}.getType());
			return CompletableFuture.completedFuture(user);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T user = doGet(hostPath, apiPath + "/" + id, new TypeToken<User>() {}.getType());
			return CompletableFuture.completedFuture(user);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> users = doGet(hostPath, apiPath,
					new TypeToken<List<User>>() {}.getType());
			
			return CompletableFuture.completedFuture(users);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}