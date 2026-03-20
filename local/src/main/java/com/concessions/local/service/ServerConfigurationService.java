package com.concessions.local.service;

import java.util.prefs.BackingStoreException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.common.service.PreferenceService;

import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.security.TokenAuthService.TokenResponse;

import jakarta.annotation.PostConstruct;

@Service
public class ServerConfigurationService {

	private static final String KEY_REFRESH_TOKEN = "refreshToken";
	private static final String KEY_SERVER_PORT = "serverPort";
	
	protected PreferenceService preferenceService;
	
	private static ServerConfiguration serverConfig;
	
	public ServerConfigurationService (@Autowired PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
		serverConfig = new ServerConfiguration();
	}
	
	@PostConstruct
	public void initialize () {
		String refreshToken = preferenceService.get(KEY_REFRESH_TOKEN);
		if (StringUtils.isNotBlank(refreshToken)) {
			TokenResponse tokenResponse = new TokenResponse(null, refreshToken, 0);
			serverConfig.setTokenResponse(tokenResponse);
		}
		
		String serverPortText = preferenceService.get(KEY_SERVER_PORT);
		if (StringUtils.isNotBlank(serverPortText)) {
			int serverPort = Integer.parseInt(serverPortText);
			serverConfig.setServerPort(serverPort);
		}
	}

	public ServerConfiguration get () {
		return serverConfig;
	}
	
	public void reset () throws BackingStoreException {
		/* HWC Once this is set we shouldn't clear it
		preferenceService.clear(KEY_DEVICE_ID);
		serverConfig.setDeviceId(null);
		*/
		preferenceService.clear(KEY_REFRESH_TOKEN);
		preferenceService.clear(KEY_SERVER_PORT);
		serverConfig.setTokenResponse(null);
		serverConfig.setServerPort(-1);
	}
	
	public void save () throws BackingStoreException {
		
		if (serverConfig.getTokenResponse() == null) {
			preferenceService.clear(KEY_REFRESH_TOKEN);
		}
		else {
			preferenceService.save(KEY_REFRESH_TOKEN, serverConfig.getTokenResponse().refresh_token());
		}
		
		if (serverConfig.getServerPort() < 1024) {
			preferenceService.clear(KEY_SERVER_PORT);
		} else {
			preferenceService.save(KEY_SERVER_PORT, String.valueOf(serverConfig.getServerPort()));
		}
		
	}
}
