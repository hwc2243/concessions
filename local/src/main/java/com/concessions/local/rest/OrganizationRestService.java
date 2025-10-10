package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.concessions.model.Organization;
import com.google.gson.reflect.TypeToken;

@Service
public class OrganizationRestService extends AbstractRestService {

	public OrganizationRestService() {
		System.out.println("OrganizationRestService instantiated");
	}

	public List<Organization> findAll () throws IOException, InterruptedException {
		return doGet("/api/external/organization/mine",new TypeToken<List<Organization>>() {}.getType());
	}
}
