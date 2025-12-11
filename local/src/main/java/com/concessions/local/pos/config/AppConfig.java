package com.concessions.local.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.pos.POSApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	public AppConfig() {
		// TODO Auto-generated constructor stub
	}

	@Bean
	public PreferenceService preferenceService ()
	{
		return new PreferenceService(POSApplication.class);
	}
	
	@Bean
	public ObjectMapper objectMapper () {
		return new ObjectMapper();
	}
}
