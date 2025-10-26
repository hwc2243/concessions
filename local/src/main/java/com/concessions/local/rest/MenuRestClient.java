package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.concessions.model.Menu;
import com.google.gson.reflect.TypeToken;

@Service
public class MenuRestClient extends AbstractRestClient {
	

	public MenuRestClient() {
		// TODO Auto-generated constructor stub
	}

	/*
	public CompletableFuture<Menu> get (long id) {
	    // 1. Use supplyAsync to execute the blocking call on a background thread
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            // 2. The blocking I/O call happens here, off the main thread
	            return doGet("/api/external/menu/" + id, new TokenType<Menu>() {}.getType());
	        } catch (IOException | InterruptedException e) {
	            // 3. If a checked exception occurs, wrap it in an unchecked CompletionException
	            // This causes the returned CompletableFuture to complete exceptionally.
	            throw new CompletionException("Failed to fetch menu ID: " + id, e);
	        }
	    })
	    // 4. Optionally, add an exceptionally handler to log the error cleanly
	    .exceptionally(ex -> {
	        System.err.println("Error fetching menu in background: " + ex.getCause().getMessage());
	        // Propagate the failure, or return null if failure should result in a null object
	        throw new CompletionException(ex.getMessage(), ex.getCause());
	    });
	}
	*/
	
	public CompletableFuture<Menu> get (long id) {
		try {
			Menu menu = doGet("/api/external/menu/" + id, new TypeToken<Menu>() {}.getType());
			return CompletableFuture.completedFuture(menu);
		} catch (IOException | InterruptedException e) {
			System.err.println("Error fetching locations in background: " + e.getMessage());
			return CompletableFuture.failedFuture(e);
		}
	}
	
	public CompletableFuture<List<Menu>> findAll () {
		try {
			List<Menu> menus = doGet("/api/external/menu",
					new TypeToken<List<Menu>>() {}.getType());
			
			return CompletableFuture.completedFuture(menus);
			
		} catch (IOException | InterruptedException e) {
			System.err.println("Error fetching locations in background: " + e.getMessage());
			return CompletableFuture.failedFuture(e);
		}
	}
}
