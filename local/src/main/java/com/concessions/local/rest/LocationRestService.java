package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.concessions.model.Location;
import com.google.gson.reflect.TypeToken;

@Service
public class LocationRestService extends AbstractRestService {

	public LocationRestService() {
	}

	public List<Location> findAll()  throws IOException, InterruptedException {
		return doGet("/api/external/location",new TypeToken<List<Location>>() {}.getType());
	}
}
