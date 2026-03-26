package com.concessions.local.bean;

import org.springframework.beans.factory.annotation.Autowired;

public class TenantDiscriminator implements com.concessions.client.rest.TenantDiscriminator {

	protected ApplicationConfiguration appConfig;
	
	public TenantDiscriminator(@Autowired ApplicationConfiguration appConfig) {
		this.appConfig = appConfig;
	}
	
	@Override
	public Long getOrganizationId () {
		return (appConfig.getOrganizationId() > 0
				? appConfig.getOrganizationId()
				: null);
	}

}
