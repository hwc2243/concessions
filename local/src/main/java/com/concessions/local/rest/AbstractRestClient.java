package com.concessions.local.rest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.Application;
import com.concessions.local.ui.model.ApplicationModel;
import com.google.gson.Gson;

public class AbstractRestClient {

	@Autowired
	protected ApplicationModel applicationModel;
	
	protected Gson gson = new Gson();

	public AbstractRestClient() {
	}

	protected <T> T doGet (String apiPath, Class<T> targetClass) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
				.uri(java.net.URI.create("http://localhost:8080" + apiPath))
				.header("Authorization", "Bearer " + applicationModel.getTokenResponse().access_token())
				.GET();
		
		if (applicationModel.getOrganizationId() > -1) {
            String orgId = String.valueOf(applicationModel.getOrganizationId());
            requestBuilder.header("organization_id", orgId);
            System.out.println("API Call with organization-id: " + orgId);
        }
		HttpRequest request	= requestBuilder.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			T result = gson.fromJson(response.body(), targetClass);
			return result;
		} else {
			throw new IOException("API Call Failed. Status: " + response.statusCode() + "\nResponse Body:\n" + response.body());
			// Return error status and body
		}
	}
	
	public <T> T doGet (String apiPath, Type targetType) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(java.net.URI.create("http://localhost:8080" + apiPath))
            .header("Authorization", "Bearer " + applicationModel.getTokenResponse().access_token())
            .GET();

        if (applicationModel.getOrganizationId() > -1) {
            String orgId = String.valueOf(applicationModel.getOrganizationId());
            requestBuilder.header("organization_id", orgId);
            System.out.println("API Call with organization-id: " + orgId);
        }
        
        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Use Type (captured by TypeToken) for correct generic deserialization
            T result = gson.fromJson(response.body(), targetType);
            return result;
        } else {
            throw new IOException(String.format(
                "API Call Failed. Status: %d. Response Body: %s", 
                response.statusCode(), 
                response.body()
            ));
        }
	}
}
