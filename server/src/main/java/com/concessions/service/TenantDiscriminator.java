package com.concessions.service;

import com.concessions.model.base.Multitenant;
import com.concessions.spring.SessionContext;

public class TenantDiscriminator implements Multitenant {

	public TenantDiscriminator() {
		// TODO Auto-generated constructor stub
	}

	public Long getOrganizationId () {
		return (SessionContext.getCurrentOrganization() != null) ? SessionContext.getCurrentOrganization().getId() : -1L;
	}
}