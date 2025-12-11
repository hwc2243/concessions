package com.concessions.local.network.manager;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DeviceManager extends AbstractManager {

	public static final String NAME = "DEVICE";
	
	public DeviceManager() {
	}
	
	@PostConstruct
	private void register ()
	{
		ManagerRegistry.registerManager(this);
	}
	
	public String getName () {
		return NAME;
	}

	@Override
	public String process(String action, String payload) {
		// TODO Auto-generated method stub
		return "Not implemented";
	}

}
