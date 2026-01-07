package com.concessions.local.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.concessions.common.network.LocalNetworkListener;
import com.concessions.common.network.ManagerRegistry;
import com.concessions.common.network.Messenger;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.pos.POSApplication;
import com.concessions.local.ui.JournalNotifier;
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
	public ManagerRegistry managerRegistry () {
		return new ManagerRegistry();
	}
	
	@Bean
	public Messenger messenger () {
		return new Messenger();
	}
	
	@Bean
	public JournalNotifier journalNotifier () {
		return new JournalNotifier();
	}
	
	@Bean(initMethod = "start", destroyMethod = "shutdown")
	public LocalNetworkListener localNetworkListener (ManagerRegistry registry, ObjectMapper mapper) {
		return new LocalNetworkListener(registry, mapper);
		
	}
}
