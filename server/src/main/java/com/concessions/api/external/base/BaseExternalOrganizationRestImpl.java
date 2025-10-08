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

import com.concessions.model.Organization;

import com.concessions.service.OrganizationService;
import com.concessions.service.ServiceException;

import com.concessions.api.external.base.BaseExternalOrganizationRest;

public abstract class BaseExternalOrganizationRestImpl implements BaseExternalOrganizationRest
{
	@Autowired
	protected OrganizationService organizationService;

	@PostMapping
	@Override
	public ResponseEntity<Organization> createOrganization (@RequestBody Organization organization)
	{
	  Organization newOrganization = null;
	  
	  try
	  {
	    newOrganization = organizationService.create(organization);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }	
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newOrganization);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Organization> deleteOrganization(@PathVariable Long id) 
    {
      try
      {
        organizationService.delete(id);
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
	public ResponseEntity<Organization> getOrganization (@PathVariable Long id)
	{
		Organization organization = null;
		
		try	{
			organization = organizationService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (organization != null) {
            return ResponseEntity.ok(organization);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Organization>> listOrganizations ()
    {
        try
        {
        	List<Organization> organizations = organizationService.findAll();
        	return ResponseEntity.ok(organizations);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Organization>> searchOrganizations (
	)
	{
	  try
	  {
	    List<Organization> allOrganizations = organizationService.findAll();
	    List<Organization> filteredOrganizations = allOrganizations.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredOrganizations);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Organization> updateOrganization(@PathVariable Long id, @RequestBody Organization organization)
    {
      Organization updatedOrganization = null;
      
      if (organization.getId() == 0) 
      {
      	organization.setId(id);
      }
      else if (id != organization.getId())
      {
         return ResponseEntity.badRequest().build();
      }
      try
      {
        Organization existingOrganization = organizationService.get(id);
        if (existingOrganization == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedOrganization = organizationService.update(organization);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedOrganization);
    }
}