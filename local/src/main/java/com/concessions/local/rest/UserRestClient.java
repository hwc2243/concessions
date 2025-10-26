package com.concessions.local.rest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.concessions.local.Application;
import com.concessions.model.User;
import com.google.gson.Gson;

public class UserRestClient extends AbstractRestClient {
	

	public UserRestClient() {
		// TODO Auto-generated constructor stub
	}

	public User me () throws IOException, InterruptedException {
		return doGet("/api/external/user/me", User.class);
	}
}
