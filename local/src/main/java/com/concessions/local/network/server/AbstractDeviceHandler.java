package com.concessions.local.network.server;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.AbstractDeviceRequestDTO;
import com.concessions.local.model.Device;
import com.concessions.local.service.DeviceService;

public abstract class AbstractDeviceHandler extends AbstractPINHandler {

	@Autowired
	protected DeviceService deviceService;
	
	public AbstractDeviceHandler() {
		// TODO Auto-generated constructor stub
	}

	protected void validateDevice (AbstractDeviceRequestDTO deviceRequest) throws ServerException {
		if (StringUtils.isBlank(deviceRequest.getDeviceId())) {
			throw new ServerException("No deviceId specified");
		}
		Device device = deviceService.fetchByDeviceId(deviceRequest.getDeviceId());
		if (device == null) {
			throw new ServerException("Failed to locate specified deviceId");
		}
		if (StringUtils.isBlank(device.getDeviceIp()) || device.getDevicePort() == 0) {
			throw new ServerException("No IP address or port for specified deviceId");
		}
	}
}
