package com.concessions.api.external.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Location;

import com.concessions.api.external.base.BaseExternalLocationRest;

public interface BaseExternalLocationRest
{
	public ResponseEntity<Location> createLocation (Location location);
	
    public ResponseEntity<Location> deleteLocation(Long id); 

	public ResponseEntity<Location> getLocation (Long id);
	
	public ResponseEntity<List<Location>> listLocations ();
	
	public ResponseEntity<List<Location>> searchLocations (
	);
	
    public ResponseEntity<Location> updateLocation (Long id, Location location);
}