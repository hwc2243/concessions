package com.concessions.local.server.model;

import org.springframework.stereotype.Component;

import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.base.model.AbstractModel;
import com.concessions.local.model.LocationConfiguration;

@Component
public class ServerApplicationModel extends AbstractModel {
	public static final String CONNECTED = "connected";
	public static final String JOURNAL = "journal";
	public static final String MENU = "menu";
	public static final String LOCATION_CONFIGURATION = "locationConfiguration";

	private boolean connected;
	
	private JournalDTO journal;
	
	private LocationConfiguration locationConfiguration;
	
	private MenuDTO menu;
	
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
}
