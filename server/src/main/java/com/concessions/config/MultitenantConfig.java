package com.concessions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.service.TenantDiscriminator;
import com.concessions.spring.SessionContext;

@Configuration
public class MultitenantConfig {

	public MultitenantConfig() {
		// TODO Auto-generated constructor stub
	}
	
	@Bean
	public TenantDiscriminator tenantDiscriminator() {
		return new TenantDiscriminator() {
			public Long getOrganizationId () {
				return (SessionContext.getCurrentOrganization() == null ? null : SessionContext.getCurrentOrganization().getId());
			}
		};
	}

}
