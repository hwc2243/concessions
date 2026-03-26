package com.concessions.local.bean;

import com.concessions.local.network.ConnectivityChecker.ConnectionStatus;
import com.concessions.local.security.TokenAuthService.TokenResponse;

import lombok.Getter;
import lombok.Setter;

public class ServerConfiguration extends AbstractConfiguration {
	
	public static final String PROPERTY_TOKEN_RESPONSE = "tokenResponse";
	public static final String PROPERTY_SERVER_IP = "serverIp";
	public static final String PROPERTY_SERVER_PORT = "serverPort";
	public static final String PROPERTY_INTERNET_CONNECTED = "internetConnected";
	public static final String PROPERTY_AUTH_CONNECTED = "authConnected";
	public static final String PROPERTY_API_CONNECTED = "apiConnected";
	
	@Getter
	private TokenResponse tokenResponse = null;
	
	@Getter
	@Setter
	private String serverIp;
	
	@Getter
	@Setter
	private int serverPort = -1;
	
	@Getter
	private ConnectionStatus internetConnected = ConnectionStatus.DISCONNECTED;
	
	@Getter
	private ConnectionStatus authConnected = ConnectionStatus.DISCONNECTED;
	
	@Getter
	private ConnectionStatus apiConnected = ConnectionStatus.DISCONNECTED;

	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldTokenResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange(PROPERTY_TOKEN_RESPONSE, oldTokenResponse, tokenResponse);
		
		updateConfigured();
	}

	public void setInternetConnected (ConnectionStatus internetConnected) {
		ConnectionStatus oldInternetConnected = this.internetConnected;
		this.internetConnected = internetConnected;
		firePropertyChange(PROPERTY_INTERNET_CONNECTED, oldInternetConnected, internetConnected);
	}
	
	public void setAuthConnected (ConnectionStatus authConnected) {
		ConnectionStatus oldAuthConnected = this.authConnected;
		this.authConnected = authConnected;
		firePropertyChange(PROPERTY_AUTH_CONNECTED, oldAuthConnected, authConnected);
	}
	
	public void setApiConnected (ConnectionStatus apiConnected) {
		ConnectionStatus oldApiConnected = this.apiConnected;
		this.apiConnected = apiConnected;
		firePropertyChange(PROPERTY_API_CONNECTED, oldApiConnected, apiConnected);
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
