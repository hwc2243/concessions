package com.concessions.local.network.dto;

public abstract class AbstractDeviceRequestDTO extends AbstractPINRequestDTO {

	private String deviceId;

	public AbstractDeviceRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
