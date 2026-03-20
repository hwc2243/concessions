package com.concessions.local.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.concessions.client.rest.base.HeaderProvider;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.BearerTokenHeaderProvider;
import com.concessions.local.bean.ClientConfiguration;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.bean.TenantDiscriminator;
import com.concessions.local.network.HandlerRegistry;
import com.concessions.local.network.LocalNetworkListener;
import com.concessions.local.pos.processor.LocalOrderProcessor;
import com.concessions.local.pos.processor.OrderProcessor;
import com.concessions.local.server.ServerApplication;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.ClientConfigurationService;
import com.concessions.local.service.ServerConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuration class for application-wide background task management.
 * Defines a Spring-managed TaskScheduler to ensure long-running background 
 * tasks, like the token polling loop, use a reliable executor pool 
 * whose lifecycle is tied directly to the Spring context.
 */
@Configuration
@ComponentScan(
	    basePackages = {
	        "com.concessions.local", 
	        "com.concessions.client", 
	        "com.concessions.common"
	    }
	)
@EnableAsync
public class AppConfig {

	@Bean
	public ApplicationConfiguration applicationConfiguration (ApplicationConfigurationService appConfigService) {
		return appConfigService.get();
	}
	
	@Bean
	public ClientConfiguration clientConfiguration (ClientConfigurationService clientConfigService) {
		return clientConfigService.get();
	}
	
	@Bean
	public ServerConfiguration serverConfiguration (ServerConfigurationService serverConfigService) {
		return serverConfigService.get();
	}
	
    /**
     * Creates a long-lived TaskScheduler bean for recurring background tasks.
     * This ensures the underlying executor is not prematurely shut down.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // Set a reasonable pool size for background tasks like polling
        scheduler.setPoolSize(5); 
        scheduler.setThreadNamePrefix("Task-");
        scheduler.initialize();
        return scheduler;
    }
    
    @Bean
    public HeaderProvider headerProvider (ServerConfiguration serverConfig) {
    	return new BearerTokenHeaderProvider(serverConfig);
    }
    
    @Bean
    public TenantDiscriminator tenantDiscriminator (ApplicationConfiguration appConfig) {
    	return new TenantDiscriminator(appConfig);
    }
    
    @Bean
    public PreferenceService preferenceService () {
    	return new PreferenceService(ServerApplication.class);
    }
    
    @Bean
    public ObjectMapper objectMapper () {
		return JsonMapper.builder()
			     .addModule(new JavaTimeModule())
			     .build();
    }
    
    // HWC TODO this assumes one common handler registry for client and server listeners
	@Bean(destroyMethod = "shutdown")
	public LocalNetworkListener clientListener (HandlerRegistry registry, ObjectMapper mapper) {
		return new LocalNetworkListener(registry, mapper, false);
		
	}
    
	@Bean(destroyMethod = "shutdown")
	public LocalNetworkListener serverListener (HandlerRegistry registry, ObjectMapper mapper) {
		return new LocalNetworkListener(registry, mapper, true);
		
	}

    @Bean
    public ApplicationModel applicationModel () {
    	return new ApplicationModel();
    }
    
    @Bean
    public OrderProcessor orderSubmissionProcessor (OrderOrchestrator orderOrchestrator) {
    	return new LocalOrderProcessor(orderOrchestrator);
    }
}