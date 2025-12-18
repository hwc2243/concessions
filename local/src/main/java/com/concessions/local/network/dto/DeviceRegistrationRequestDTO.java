package com.concessions.local.network.dto;

import com.concessions.local.model.DeviceTypeType;

public class DeviceRegistrationRequestDTO extends AbstractDeviceRequestDTO {

	protected DeviceTypeType deviceType;
	
	protected String deviceIp;
	
	protected int devicePort;
	
	public DeviceRegistrationRequestDTO() {
	}

	public DeviceTypeType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeType deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public int getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(int devicePort) {
		this.devicePort = devicePort;
	}
}
