package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import com.concessions.model.Location;
import com.google.gson.reflect.TypeToken;

public class LocationService extends AbstractRestService {

	public LocationService() {
	}

	public List<Location> findAll()  throws IOException, InterruptedException {
		return doGet("/api/external/location",new TypeToken<List<Location>>() {}.getType());
	}
}
