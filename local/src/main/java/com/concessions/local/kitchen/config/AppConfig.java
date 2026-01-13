package com.concessions.local.kitchen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.common.network.HandlerRegistry;
import com.concessions.common.network.Messenger;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.kitchen.KitchenApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class AppConfig {

	public AppConfig() {
		// TODO Auto-generated constructor stub
	}

	@Bean
	public PreferenceService preferenceService ()
	{
		return new PreferenceService(KitchenApplication.class);
	}
	
	@Bean
	public ObjectMapper objectMapper () {
		return JsonMapper.builder()
			     .addModule(new JavaTimeModule())
			     .build();
	}
	
	@Bean
	public HandlerRegistry managerRegistry () {
		return new HandlerRegistry();
	}
	
	@Bean
	public Messenger messenger () {
		return new Messenger();
	}
}
