package com.concessions.local.bean;

import com.concessions.local.security.TokenAuthService.TokenResponse;

public class ServerConfiguration extends AbstractConfiguration {
	
	public static final String PROPERTY_TOKEN_RESPONSE = "tokenResponse";
	
	private TokenResponse tokenResponse;
	
	public TokenResponse getTokenResponse() {
		return tokenResponse;
	}

	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldTokenResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange(PROPERTY_TOKEN_RESPONSE, oldTokenResponse, tokenResponse);
		updateConfigured();
	}


	@Override
	void updateConfigured() {
		boolean oldConfigured = configured;
		configured = (tokenResponse != null);
		firePropertyChange(PROPERTY_CONFIGURED, oldConfigured, configured);
	}
}
