package com.concessions.local.network.dto;

import com.concessions.local.model.DeviceTypeType;

public class DeviceRegistrationRequestDTO {

	private String deviceId;
	private DeviceTypeType deviceType;
	private int pin;
	
	public DeviceRegistrationRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceTypeType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeType deviceType) {
		this.deviceType = deviceType;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

}
