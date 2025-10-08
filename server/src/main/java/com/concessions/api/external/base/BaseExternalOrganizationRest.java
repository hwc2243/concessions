package com.concessions.api.external.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Organization;

import com.concessions.api.external.base.BaseExternalOrganizationRest;

public interface BaseExternalOrganizationRest
{
	public ResponseEntity<Organization> createOrganization (Organization organization);
	
    public ResponseEntity<Organization> deleteOrganization(Long id); 

	public ResponseEntity<Organization> getOrganization (Long id);
	
	public ResponseEntity<List<Organization>> listOrganizations ();
	
	public ResponseEntity<List<Organization>> searchOrganizations (
	);
	
    public ResponseEntity<Organization> updateOrganization (Long id, Organization organization);
}