package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Address;

import com.google.gson.reflect.TypeToken;

public abstract class BaseAddressRestClient<T extends Address>
  extends AbstractRestClient
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/address";

	public BaseAddressRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T address = doPost(hostPath, apiPath, entity, new TypeToken<Address>() {}.getType());
			return CompletableFuture.completedFuture(address);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T address = doGet(hostPath, apiPath + "/" + id, new TypeToken<Address>() {}.getType());
			return CompletableFuture.completedFuture(address);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> addresss = doGet(hostPath, apiPath,
					new TypeToken<List<Address>>() {}.getType());
			
			return CompletableFuture.completedFuture(addresss);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}