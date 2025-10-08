package com.concessions.controller;

import java.util.Collection;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.concessions.model.Organization;
import com.concessions.model.User;
import com.concessions.spring.SessionContext;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

	// Inject the application name from application.properties
	@Value("${application.name:Concessions Management System}")
	private String applicationName;

	// Inject the application version from application.properties
	@Value("${application.version:SNAPSHOT}")
	private String applicationVersion;

	public AbstractController() {
		// TODO Auto-generated constructor stub
	}

	// Add the application name to the model
	@ModelAttribute("appName")
	public String getApplicationName() {
		return this.applicationName;
	}

	// Add the application version to the model
	@ModelAttribute("appVersion")
	public String getApplicationVersion() {
		return this.applicationVersion;
	}

	@ModelAttribute("currentUser")
	public User getCurrentUser() {
		// Retrieve the user from the ThreadLocal
		logger.info("Getting current user from session context: " + SessionContext.getCurrentUser());
		return SessionContext.getCurrentUser();
	}
	
	@ModelAttribute("currentOrganization")
	public Organization getCurrentOrganization() {
		return SessionContext.getCurrentOrganization();
	}
	
	@ModelAttribute("requestURI")
	public String getRequestURI (HttpServletRequest request) {
		logger.info("Request URI: " + request.getRequestURI());
		return request.getRequestURI();
	}
	@ModelAttribute("userOrganizations")
	public Collection<Organization> getUserOrganizations() {
		TreeSet<Organization> organizations = new TreeSet<>();
		
		User currentUser = SessionContext.getCurrentUser();
		if (currentUser != null) {
			if (currentUser.getOrganization() != null) {
				organizations.add(currentUser.getOrganization());
			}
			if (currentUser.getOrganizations() != null) {
				organizations.addAll(currentUser.getOrganizations());
			}
		}
		
		return organizations;
	}
	
	@ModelAttribute("module")
	public abstract String getModule ();
}
