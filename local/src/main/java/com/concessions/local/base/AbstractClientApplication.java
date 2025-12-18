package com.concessions.local.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.RegistrationClient;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.base.ui.PINController;
import com.concessions.local.network.Messenger;
import com.concessions.local.network.client.LocalNetworkClient;

public abstract class AbstractClientApplication extends AbstractApplication {

	@Autowired
	protected LocalNetworkClient localNetworkClient;
	
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
