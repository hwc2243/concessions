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
import com.concessions.local.bean.BearerTokenHeaderProvider;
import com.concessions.local.bean.TenantDiscriminator;
import com.concessions.local.pos.processor.LocalOrderSubmissionProcessor;
import com.concessions.local.pos.processor.OrderSubmissionProcessor;
import com.concessions.local.server.ServerApplication;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
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
	    },
	    // Exclude the 'com.concessions.local.network' package and all its sub-packages
	    excludeFilters = @Filter(
	        type = FilterType.REGEX,
	        // The regex pattern matches the package and any class inside it (.*)
	        pattern = "com\\.concessions\\.local\\.network\\.client\\..*" 
	    )
	)
@EnableAsync
public class AppConfig {

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
    public HeaderProvider headerProvider (ServerApplicationModel model) {
    	return new BearerTokenHeaderProvider(model);
    }
    
    @Bean
    public TenantDiscriminator tenantDiscriminator (ServerApplicationModel model) {
    	return new TenantDiscriminator(model);
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
    
    @Bean
    public OrderSubmissionProcessor orderSubmissionProcessor (OrderOrchestrator orderOrchestrator) {
    	return new LocalOrderSubmissionProcessor(orderOrchestrator);
    }
}