package com.concessions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class ServerApplication {
	private final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
	
	@Value("${application.version:SNAPSHOT}")
	protected String version;

	@Value("${application.name:Concession Server}")
	protected String name;
	
	public ServerApplication() {
	}
	
	@PostConstruct
	public void init() {
		logger.info("Starting {} version {}", name, version);
	}
	
	/**
	 * Main method to start the Spring Boot application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
