package com.concessions.local.service;

import java.util.prefs.BackingStoreException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ApplicationConfiguration.ApplicationRole;

import jakarta.annotation.PostConstruct;

@Service
public class ApplicationConfigurationService {

	private static final String KEY_APPLICATION_ROLE = "applicationRole";
	
	protected PreferenceService preferenceService;
	
	private static ApplicationConfiguration applicationConfiguration;
	
	public ApplicationConfigurationService (@Autowired PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
		this.applicationConfiguration = new ApplicationConfiguration();
	}
	
	@PostConstruct
	public void initialize () {
		String applicationRole = preferenceService.get(KEY_APPLICATION_ROLE);

		if (StringUtils.isNotBlank(applicationRole)) {
        	applicationConfiguration.setApplicationRole(ApplicationRole.valueOf(applicationRole));
	    } else {
	    	applicationConfiguration.setApplicationRole(ApplicationRole.UNDECIDED);
	    }	
	}

	public ApplicationConfiguration get () {
		return applicationConfiguration;
	}
	
	public void reset () throws BackingStoreException {
		preferenceService.clear(KEY_APPLICATION_ROLE);
		applicationConfiguration.setApplicationRole(ApplicationRole.UNDECIDED);
	}
	
	public void save () throws BackingStoreException {
		if (applicationConfiguration.isConfigured()) {
			preferenceService.save(KEY_APPLICATION_ROLE, applicationConfiguration.getApplicationRole().toString());
		}
	}
}
