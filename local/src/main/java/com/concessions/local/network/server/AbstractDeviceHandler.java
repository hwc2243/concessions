package com.concessions.local.network.server;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.AbstractDeviceRequestDTO;
import com.concessions.local.dto.DeviceDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.model.Device;
import com.concessions.local.service.DeviceMapper;
import com.concessions.local.service.DeviceService;

public abstract class AbstractDeviceHandler extends AbstractPINHandler {

	@Autowired
	protected DeviceMapper deviceMapper;
	
	@Autowired
	protected DeviceService deviceService;
	
	public AbstractDeviceHandler() {
		// TODO Auto-generated constructor stub
	}

	protected void validateDevice(AbstractDeviceRequestDTO deviceRequest, DeviceTypeType... allowedTypes) throws ServerException {
	    // 1. Initial validation of request
	    if (StringUtils.isBlank(deviceRequest.getDeviceId())) {
	        throw new ServerException("No deviceId specified");
	    }

	    // 2. Fetch and validate existence
	    DeviceDTO device = loadDevice(deviceRequest.getDeviceId());
	    if (device == null) {
	        throw new ServerException("Failed to locate specified deviceId");
	    }

	    // 3. Connectivity validation
	    if (StringUtils.isBlank(device.getDeviceIp()) || device.getDevicePort() == 0) {
	        throw new ServerException("No IP address or port for specified deviceId");
	    }

	    // 4. Device Type Validation via Varargs
	    // If the caller provided any types, we ensure the device matches one of them.
	    if (allowedTypes != null && allowedTypes.length > 0) {
	        boolean matchFound = false;
	        for (DeviceTypeType type : allowedTypes) {
	            if (device.getDeviceType() == type) {
	                matchFound = true;
	                break;
	            }
	        }

	        if (!matchFound) {
	            throw new ServerException("Device is not valid type to receive orders");
	        }
	    }
	}
	
	protected DeviceDTO loadDevice (String deviceId) {
	    Device device = deviceService.fetchByDeviceId(deviceId);
	    return (device == null ? null : deviceMapper.toDto(device));
	}
}
