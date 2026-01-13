package com.concessions.local.server.model;

import org.springframework.stereotype.Component;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.base.model.AbstractModel;
import com.concessions.local.base.model.POSModel;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.security.TokenAuthService.TokenResponse;

@Component
public class ServerApplicationModel extends AbstractModel implements POSModel {
	public static final String CONNECTED = "connected";
	public static final String JOURNAL = "journal";
	public static final String MENU = "menu";
	public static final String LOCATION_CONFIGURATION = "locationConfiguration";
	public static final String TOKEN_RESPONSE = "tokenResponse";

	private boolean connected;
	
	private JournalDTO journal;
	
	private LocationConfiguration locationConfiguration;
	
	private MenuDTO menu;
	
	private long organizationId = -1;
	
	private String pin;
	
	private TokenResponse tokenResponse;
	
	public ServerApplicationModel() {
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		boolean oldConnected = this.connected;
		this.connected = connected;
		firePropertyChange(CONNECTED, oldConnected, connected);
	}

	public JournalDTO getJournal ()
	{
		return this.journal;		
	}
	
	public void setJournal (JournalDTO journal)
	{
		JournalDTO oldJournal = this.journal;
		this.journal = journal;
		firePropertyChange(JOURNAL, oldJournal, journal);
	}
	
	public MenuDTO getMenu() {
		return menu;
	}

	public void setMenu (MenuDTO menu) {
		MenuDTO oldMenu = this.menu;
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
	
	public TokenResponse getTokenResponse() {
		return tokenResponse;
	}
	
	public void setTokenResponse(TokenResponse tokenResponse) {
		TokenResponse oldResponse = this.tokenResponse;
		this.tokenResponse = tokenResponse;
		firePropertyChange(TOKEN_RESPONSE, oldResponse, tokenResponse);
	}
}
