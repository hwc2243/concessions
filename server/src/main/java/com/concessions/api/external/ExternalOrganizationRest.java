package com.concessions.api.external;

import com.concessions.model.Organization;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.concessions.api.external.base.BaseExternalOrganizationRest;

public interface ExternalOrganizationRest extends BaseExternalOrganizationRest
{
	public ResponseEntity<List<Organization>> mine ();

}