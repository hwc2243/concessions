package com.concessions.local.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.network.LocalNetworkListener;
import com.concessions.local.server.RegistrationState;
import com.concessions.local.ui.controller.PINController;

public abstract class AbstractClientApplication extends AbstractApplication {

	@Autowired
	protected LocalNetworkListener localNetworkListener;
	
	@Autowired
	protected PINController pinController;

	@Autowired
	protected PreferenceService preferenceService;
	
	@Autowired
	protected RegistrationState registrationClient;
	
	public AbstractClientApplication() {
		super();
	}


}
