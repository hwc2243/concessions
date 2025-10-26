package com.concessions.local.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class for application-wide background task management.
 * Defines a Spring-managed TaskScheduler to ensure long-running background 
 * tasks, like the token polling loop, use a reliable executor pool 
 * whose lifecycle is tied directly to the Spring context.
 */
@Configuration
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
}