package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.concessions.model.Organization;
import com.google.gson.reflect.TypeToken;

@Service
public class OrganizationRestClient extends AbstractRestClient {

	public OrganizationRestClient() {
		System.out.println("OrganizationRestService instantiated");
	}

	@Async
	public CompletableFuture<List<Organization>> findAll ()
	{
		try {
			List<Organization> organizations = doGet("/api/external/organization/mine",
					new TypeToken<List<Organization>>() {}.getType());
			
			return CompletableFuture.completedFuture(organizations);
			
		} catch (IOException | InterruptedException e) {
			System.err.println("Error fetching organizations in background: " + e.getMessage());
			return CompletableFuture.failedFuture(e);
		}
	}
}
