package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Location;

import com.google.gson.reflect.TypeToken;

public abstract class BaseLocationRestClient<T extends Location>
  extends MultitenantRestClient  
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/location";

	public BaseLocationRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T location = doPost(hostPath, apiPath, entity, new TypeToken<Location>() {}.getType());
			return CompletableFuture.completedFuture(location);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T location = doGet(hostPath, apiPath + "/" + id, new TypeToken<Location>() {}.getType());
			return CompletableFuture.completedFuture(location);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> locations = doGet(hostPath, apiPath,
					new TypeToken<List<Location>>() {}.getType());
			
			return CompletableFuture.completedFuture(locations);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}