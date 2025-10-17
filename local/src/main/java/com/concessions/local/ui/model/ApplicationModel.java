package com.concessions.local.ui.model;

import org.springframework.stereotype.Component;

import com.concessions.local.service.TokenAuthService.TokenResponse;

@Component
public class ApplicationModel extends AbstractModel {

	private String statusMessage;
	private TokenResponse tokenResponse;
	
	public ApplicationModel() {
		// TODO Auto-generated constructor stub
	}

	public String getStatusMessage() {
		return statusMessage;
	}
	
	public void setStatusMessage(String statusMessage) {
		String oldMessage = this.statusMessage;
		this.statusMessage = statusMessage;
		firePropertyChange("statusMessage", oldMessage, statusMessage);
	}
	
	public TokenResponse getTokenResponse() {
		return tokenResponse;
	}
	
	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange("tokenResponse", oldResponse, tokenResponse);
	}
}
