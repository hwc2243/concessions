package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.concessions.model.Location;
import com.concessions.model.Organization;
import com.google.gson.reflect.TypeToken;

@Service
public class LocationRestClient extends AbstractRestClient {

	public LocationRestClient() {
	}

	@Async
	public CompletableFuture<List<Location>> findAll() {
		try {
			List<Location> locations = doGet("/api/external/location",
					new TypeToken<List<Location>>() {}.getType());
			
			return CompletableFuture.completedFuture(locations);
			
		} catch (IOException | InterruptedException e) {
			System.err.println("Error fetching locations in background: " + e.getMessage());
			return CompletableFuture.failedFuture(e);
		}
	}
}
