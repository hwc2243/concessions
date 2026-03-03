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
	}

	public ServerConfiguration get () {
		return serverConfig;
	}
	
	public void reset () throws BackingStoreException {
		preferenceService.clear(KEY_REFRESH_TOKEN);
		serverConfig.setTokenResponse(null);
	}
	
	public void save () throws BackingStoreException {
		if (serverConfig.getTokenResponse() == null) {
			preferenceService.clear(KEY_REFRESH_TOKEN);
		}
		else {
			preferenceService.save(KEY_REFRESH_TOKEN, serverConfig.getTokenResponse().refresh_token());
		}
	}
}
