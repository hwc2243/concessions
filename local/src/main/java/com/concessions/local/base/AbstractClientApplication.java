package com.concessions.local.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.LocalNetworkListener;
import com.concessions.common.network.RegistrationClient;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.base.ui.PINController;

public abstract class AbstractClientApplication extends AbstractApplication {

	@Autowired
	protected LocalNetworkListener localNetworkListener;
	
	@Autowired
	protected PINController pinController;

	@Autowired
	protected PreferenceService preferenceService;
	
	@Autowired
	protected RegistrationClient registrationClient;
	
	public AbstractClientApplication() {
		super();
	}


}
