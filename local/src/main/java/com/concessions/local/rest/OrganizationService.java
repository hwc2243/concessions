package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import com.concessions.model.Organization;
import com.google.gson.reflect.TypeToken;

public class OrganizationService extends AbstractRestService {

	public OrganizationService() {
		// TODO Auto-generated constructor stub
	}

	public List<Organization> mine () throws IOException, InterruptedException {
		return doGet("/api/external/organization/mine",new TypeToken<List<Organization>>() {}.getType());
	}
}
