package com.concessions.client.rest.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.rest.TenantDiscriminator;

public abstract class MultitenantRestClient extends AbstractRestClient {

	@Autowired
	protected TenantDiscriminator tenantDiscriminator;
	
	public MultitenantRestClient() {
		super();
	}

	@Override
	protected Map<String, String> getHeaders() {
		Map<String,String> headers = new HashMap<>(super.getHeaders());
		headers.put("organizationId", "" + tenantDiscriminator.getOrganizationId());
		return headers;
	}

}