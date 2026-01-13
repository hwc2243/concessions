package com.concessions.common.network;

public interface NetworkConstants {
	
	// Configuration service
	public String CONFIGURATION_SERVICE = "CONFIG";
	public String CONFIGURATION_LOCATION_ACTION = "LOCATION";
	
	// Device service
	public String DEVICE_SERVICE = "DEVICE";
	public String DEVICE_REGISTER_ACTION = "REGISTER";
	
	// Health Check Service
	public String HEALTH_SERVICE = "HEALTH";
	public String HEALTH_CHECK_ACTION = "CHECK";

	// Journal Service
	public String JOURNAL_SERVICE = "JOURNAL";
	public String JOURNAL_GET_ACTION = "GET";
	public String JOURNAL_CHANGE_ACTION = "CHANGE";

	// Menu service
	public String MENU_SERVICE = "MENU";
	public String MENU_GET_ACTION = "GET";
	
	// Order Service
	public String ORDER_SERVICE = "ORDER";
	public String ORDER_SUBMIT_ACTION = "SUBMIT";
	
	// PIN Service
	public String PIN_SERVICE = "PIN";
	public String PIN_VERIFY_ACTION = "VERIFY";
}
