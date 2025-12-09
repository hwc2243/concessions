package com.concessions.local.bean;

import com.concessions.local.ui.model.ApplicationModel;

public class TenantDiscriminator implements com.concessions.client.rest.TenantDiscriminator {

	protected ApplicationModel model;
	
	public TenantDiscriminator(ApplicationModel model) {
		this.model = model;
	}
	
	@Override
	public Long getOrganizationId () {
		return (model.getOrganizationId() > 0 ? model.getOrganizationId() : null);
	}

}
