package com.concessions.local.network.dto;

public class ConfigurationResponseDTO {

	protected String organizationName;
	
	protected String locationName;
	
	protected String menuName;
	
	public ConfigurationResponseDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

}
