package com.concessions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.service.TenantDiscriminator;

@Configuration
public class MultitenantConfig {

	public MultitenantConfig() {
		// TODO Auto-generated constructor stub
	}
	
	@Bean
	public TenantDiscriminator tenantDiscriminator() {
		return new TenantDiscriminator();
	}

}
