package com.concessions.api.external.base;

import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Location;

import com.concessions.service.LocationService;
import com.concessions.service.ServiceException;

import com.concessions.api.external.base.BaseExternalLocationRest;

public abstract class BaseExternalLocationRestImpl implements BaseExternalLocationRest
{
	@Autowired
	protected LocationService locationService;

	@PostMapping
	@Override
	public ResponseEntity<Location> createLocation (@RequestBody Location location)
	{
	  Location newLocation = null;
	  
	  try
	  {
	    newLocation = locationService.create(location);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }	
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newLocation);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Location> deleteLocation(@PathVariable Long id) 
    {
      try
      {
        locationService.delete(id);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.noContent().build();
    }

	@GetMapping("/{id}")
	@Override
	public ResponseEntity<Location> getLocation (@PathVariable Long id)
	{
		Location location = null;
		
		try	{
			location = locationService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Location>> listLocations ()
    {
        try
        {
        	List<Location> locations = locationService.findAll();
        	return ResponseEntity.ok(locations);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Location>> searchLocations (
	)
	{
	  try
	  {
	    List<Location> allLocations = locationService.findAll();
	    List<Location> filteredLocations = allLocations.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredLocations);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location)
    {
      Location updatedLocation = null;

      if (location.getId() == 0) 
      {
      	location.setId(id);
      }
      else if (id != location.getId())
      {
        return ResponseEntity.badRequest().build();
      }
      try
      {
        Location existingLocation = locationService.get(id);
        if (existingLocation == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedLocation = locationService.update(location);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedLocation);
    }
}