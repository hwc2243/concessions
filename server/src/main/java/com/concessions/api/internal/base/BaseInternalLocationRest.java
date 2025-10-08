package com.concessions.api.internal.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Location;

import com.concessions.api.internal.base.BaseInternalLocationRest;

public interface BaseInternalLocationRest
{
	public ResponseEntity<Location> createLocation (Location location);
	
    public ResponseEntity<Location> deleteLocation(Long id); 

	public ResponseEntity<Location> getLocation (Long id);
	
	public ResponseEntity<List<Location>> listLocations ();
	
	public ResponseEntity<List<Location>> searchLocations (
	);
	
    public ResponseEntity<Location> updateLocation (Long id, Location location);
}