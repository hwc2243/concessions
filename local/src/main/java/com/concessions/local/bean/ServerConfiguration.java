package com.concessions.local.bean;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.security.TokenAuthService.TokenResponse;

public class ServerConfiguration extends AbstractConfiguration {
	
	public static final String PROPERTY_TOKEN_RESPONSE = "tokenResponse";
	public static final String PROPERTY_SERVER_PORT = "serverPort";
	
	private TokenResponse tokenResponse = null;
	private long organizationId = -1;
	private String serverIp;
	private int serverPort = -1;

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public TokenResponse getTokenResponse() {
		return tokenResponse;
	}

	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldTokenResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange(PROPERTY_TOKEN_RESPONSE, oldTokenResponse, tokenResponse);
		
		updateConfigured();
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	@Override
	protected void updateConfigured() {
		boolean oldConfigured = configured;
		
		configured = (tokenResponse != null);
		
		if (oldConfigured != configured) {
			firePropertyChange(PROPERTY_CONFIGURED, oldConfigured, configured);
		}
	}
}
