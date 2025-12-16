package com.concessions.local.bean;

import com.concessions.local.server.model.ServerApplicationModel;

public class TenantDiscriminator implements com.concessions.client.rest.TenantDiscriminator {

	protected ServerApplicationModel model;
	
	public TenantDiscriminator(ServerApplicationModel model) {
		this.model = model;
	}
	
	@Override
	public Long getOrganizationId () {
		return (model.getOrganizationId() > 0 ? model.getOrganizationId() : null);
	}

}
