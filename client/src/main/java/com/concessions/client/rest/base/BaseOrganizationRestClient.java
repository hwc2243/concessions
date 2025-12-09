package com.concessions.client.rest.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.concessions.client.model.Organization;

import com.google.gson.reflect.TypeToken;

public abstract class BaseOrganizationRestClient<T extends Organization>
  extends AbstractRestClient
{
	protected String hostPath = "http://localhost:8080";
	protected String apiPath = "/api/external/organization";

	public BaseOrganizationRestClient() {
		super();
	}
	
	public CompletableFuture<T> create (T entity) {
		try {
			T organization = doPost(hostPath, apiPath, entity, new TypeToken<Organization>() {}.getType());
			return CompletableFuture.completedFuture(organization);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<T> get (long id) {
		try {
			T organization = doGet(hostPath, apiPath + "/" + id, new TypeToken<Organization>() {}.getType());
			return CompletableFuture.completedFuture(organization);
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<T>> findAll () {
		try {
			List<T> organizations = doGet(hostPath, apiPath,
					new TypeToken<List<Organization>>() {}.getType());
			
			return CompletableFuture.completedFuture(organizations);
			
		} catch (IOException | InterruptedException e) {
			return CompletableFuture.failedFuture(e);
		}
	}
}