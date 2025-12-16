package com.concessions.local.base.model;

public abstract class AbstractClientModel extends AbstractLocationConfigurationModel {

	protected String deviceId;
	protected String deviceNumber;
	protected String pin;
	
	public AbstractClientModel() {
		// TODO Auto-generated constructor stub
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	
	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
