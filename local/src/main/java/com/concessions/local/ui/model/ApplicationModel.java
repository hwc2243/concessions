package com.concessions.local.ui.model;

import org.springframework.stereotype.Component;

import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.client.model.Journal;
import com.concessions.client.model.Menu;

@Component
public class ApplicationModel extends AbstractModel {
	public static final String CONNECTED = "connected";
	public static final String JOURNAL = "journal";
	public static final String MENU = "menu";
	public static final String LOCATION_CONFIGURATION = "locationConfiguration";
	public static final String STATUS_MESSAGE = "statusMessage";
	public static final String TOKEN_RESPONSE = "tokenResponse";

	private String statusMessage;
	
	private boolean connected;
	
	private Journal journal;
	
	private LocationConfiguration locationConfiguration;
	
	private Menu menu;
	
	private long organizationId = -1;
	
	private String pin;
	
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

	public Journal getJournal ()
	{
		return this.journal;		
	}
	
	public void setJournal (Journal journal)
	{
		Journal oldJournal = this.journal;
		this.journal = journal;
		firePropertyChange(JOURNAL, oldJournal, journal);
	}
	
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		Menu oldMenu = this.menu;
		this.menu = menu;
		firePropertyChange(MENU, oldMenu, menu);
	}

	public LocationConfiguration getLocationConfiguration() {
		return locationConfiguration;
	}

	public void setLocationConfiguration(LocationConfiguration locationConfiguration) {
		LocationConfiguration oldLocationConfiguration = this.locationConfiguration;
		this.locationConfiguration = locationConfiguration;
		firePropertyChange(LOCATION_CONFIGURATION, oldLocationConfiguration, locationConfiguration);
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getPIN ()	{
		return this.pin;
	}
	
	public void setPIN (String pin) {
		this.pin = pin;
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
