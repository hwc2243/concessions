package com.concessions.local.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.common.event.JournalNotifier;
import com.concessions.common.network.LocalNetworkListener;
import com.concessions.common.network.HandlerRegistry;
import com.concessions.common.network.Messenger;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.pos.POSApplication;
import com.concessions.local.pos.model.POSApplicationModel;
import com.concessions.local.pos.processor.NetworkOrderSubmissionProcessor;
import com.concessions.local.pos.processor.OrderSubmissionProcessor;
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
		return new PreferenceService(POSApplication.class);
	}
	
	@Bean
	public ObjectMapper objectMapper () {
		return JsonMapper.builder()
			     .addModule(new JavaTimeModule())
			     .build();
	}
	
	@Bean
	public HandlerRegistry handlerRegistry () {
		return new HandlerRegistry();
	}
	
	@Bean
	public JournalNotifier journalNotifier () {
		return new JournalNotifier();
	}
	
	@Bean
	public Messenger messenger () {
		return new Messenger();
	}

	@Bean
	public OrderSubmissionProcessor orderSubmissionProcessor (POSApplicationModel model, Messenger messenger)
	{
		return new NetworkOrderSubmissionProcessor(model, messenger);
	}
	
	@Bean(initMethod = "start", destroyMethod = "shutdown")
	public LocalNetworkListener localNetworkListener (HandlerRegistry registry, ObjectMapper mapper) {
		return new LocalNetworkListener(registry, mapper);
		
	}
}
