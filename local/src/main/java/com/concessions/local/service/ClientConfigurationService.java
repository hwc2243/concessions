package com.concessions.local.service;

import java.util.prefs.BackingStoreException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ClientConfiguration;
import com.nimbusds.oauth2.sdk.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Service
public class ClientConfigurationService {
	private static final String KEY_SERVER_IP = "serverIp";
	private static final String KEY_SERVER_PORT = "serverPort";
	
	protected PreferenceService preferenceService;
	
	private static ClientConfiguration clientConfig = new ClientConfiguration();

	public ClientConfigurationService (@Autowired PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
	}
	
	@PostConstruct
	protected void initialize () {
		String serverIp = preferenceService.get(KEY_SERVER_IP);
		if (StringUtils.isNotBlank(serverIp)) {
			clientConfig.setServerIp(serverIp);
		}
		
		String serverPortText = preferenceService.get(KEY_SERVER_PORT);
		if (StringUtils.isNotBlank(serverPortText)) {
			int serverPort = Integer.parseInt(serverPortText);
			clientConfig.setServerPort(serverPort);
		}
	}
	
	public ClientConfiguration get () {
		return clientConfig;
	}
	
	public void reset () throws BackingStoreException {
		preferenceService.clear(KEY_SERVER_IP);
		preferenceService.clear(KEY_SERVER_PORT);
		
		clientConfig.setServerIp(null);
		clientConfig.setServerPort(-1);
	}
	
	public void save () throws BackingStoreException {
		if (StringUtils.isBlank(clientConfig.getServerIp())) {
			preferenceService.clear(KEY_SERVER_IP);
		} else {
			preferenceService.save(KEY_SERVER_IP, clientConfig.getServerIp());
		}
		
		if (clientConfig.getServerPort() < 1024) {
			preferenceService.clear(KEY_SERVER_PORT);
		} else {
			preferenceService.save(KEY_SERVER_PORT, String.valueOf(clientConfig.getServerPort()));
		}
	}
}
