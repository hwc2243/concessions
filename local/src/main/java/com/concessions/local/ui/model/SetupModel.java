package com.concessions.local.ui.model;

import com.concessions.client.model.Location;
import com.concessions.client.model.Menu;
import com.concessions.client.model.Organization;
import com.concessions.local.base.model.AbstractModel;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SetupModel extends AbstractModel {

	protected List<Organization> organizations;
	protected List<Location> locations;
	protected List<Menu> menus;
	
	public SetupModel() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Organization> getOrganizations() {
		return organizations;
	}
	
	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
		firePropertyChange("organizations", null, organizations);
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		System.out.println("Setting locations in model: " + locations.size());
		this.locations = locations;
		firePropertyChange("locations", null, locations);
	}

	public List<Menu> getMenus() {
		return menus;
	}
	
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
		firePropertyChange("menus", null, menus);
	}
}
