package com.concessions.local.network.dto;

import com.concessions.local.model.DeviceTypeType;

public class DeviceRegistrationRequestDTO extends AbstractDeviceRequestDTO {

	private DeviceTypeType deviceType;
	
	public DeviceRegistrationRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	public DeviceTypeType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeType deviceType) {
		this.deviceType = deviceType;
	}
}
