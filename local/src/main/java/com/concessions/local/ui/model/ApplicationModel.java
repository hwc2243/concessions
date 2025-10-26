package com.concessions.local.ui.model;

import org.springframework.stereotype.Component;

import com.concessions.local.model.OrganizationConfiguration;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.model.Menu;

@Component
public class ApplicationModel extends AbstractModel {
	public static final String CONNECTED = "connected";
	public static final String MENU = "menu";
	public static final String ORGANIZATION_CONFIGURATION = "organizationConfiguration";
	public static final String STATUS_MESSAGE = "statusMessage";
	public static final String TOKEN_RESPONSE = "tokenResponse";

	private String statusMessage;
	
	private boolean connected;
	
	private Menu menu;
	
	private OrganizationConfiguration organizationConfiguration;
	
	private long organizationId = -1;
	
	private TokenResponse tokenResponse;
	
	public ApplicationModel() {
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		boolean oldConnected = this.connected;
		this.connected = connected;
		firePropertyChange(CONNECTED, oldConnected, connected);
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		Menu oldMenu = this.menu;
		this.menu = menu;
		firePropertyChange(MENU, oldMenu, menu);
	}

	public OrganizationConfiguration getOrganizationConfiguration() {
		return organizationConfiguration;
	}

	public void setOrganizationConfiguration(OrganizationConfiguration organizationConfiguration) {
		OrganizationConfiguration oldOrganizationConfiguration = this.organizationConfiguration;
		this.organizationConfiguration = organizationConfiguration;
		firePropertyChange(ORGANIZATION_CONFIGURATION, oldOrganizationConfiguration, organizationConfiguration);
	}

	
	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
	
	public void setStatusMessage(String statusMessage) {
		String oldMessage = this.statusMessage;
		this.statusMessage = statusMessage;
		firePropertyChange(STATUS_MESSAGE, oldMessage, statusMessage);
	}
	
	public TokenResponse getTokenResponse() {
		return tokenResponse;
	}
	
	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange(TOKEN_RESPONSE, oldResponse, tokenResponse);
	}
}
