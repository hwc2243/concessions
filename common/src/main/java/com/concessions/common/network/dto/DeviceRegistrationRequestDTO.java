package com.concessions.common.network.dto;

public class DeviceRegistrationRequestDTO extends AbstractDeviceRequestDTO {

	protected String deviceType;
	
	protected String deviceIp;
	
	protected int devicePort;
	
	public DeviceRegistrationRequestDTO() {
	}

	public DeviceRegistrationRequestDTO (String pin, String deviceId, String deviceType, String deviceIp, int devicePort) {
		this.pin = pin;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.deviceIp = deviceIp;
		this.devicePort = devicePort;
				
	}
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
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
