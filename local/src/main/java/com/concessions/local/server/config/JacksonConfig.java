package com.concessions.local.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

	public JacksonConfig() {
		// TODO Auto-generated constructor stub
	}

	@Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register support for Java 8 Date/Time API (LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());
        
        // Prevent Java from turning dates into arrays like [2026, 2, 16, ...]
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
