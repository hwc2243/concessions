package com.concessions.local.bean;

import org.springframework.beans.factory.annotation.Autowired;

public class TenantDiscriminator implements com.concessions.client.rest.TenantDiscriminator {

	protected ApplicationConfiguration appConfig;
	
	public TenantDiscriminator(@Autowired ApplicationConfiguration appConfig) {
		this.appConfig = appConfig;
	}
	
	@Override
	public Long getOrganizationId () {
		return ((appConfig.getLocationConfiguration() != null && appConfig.getLocationConfiguration().getOrganizationId() > 0)
				? appConfig.getLocationConfiguration().getOrganizationId()
				: null);
	}

}
