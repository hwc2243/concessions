package com.concessions.local.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.service.ServiceException;
import com.concessions.common.network.AbstractManager;
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO;
import com.concessions.common.network.dto.DeviceRegistrationResponseDTO;
import com.concessions.local.model.Device;
import com.concessions.local.model.DeviceTypeType;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.service.DeviceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class DeviceManager extends AbstractPINManager {
	private static final Logger logger = LoggerFactory.getLogger(DeviceManager.class);

	@Autowired
	protected DeviceService deviceService;
	
	public DeviceManager() {
	}
	
	public String getName () {
		return DEVICE_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws ServerException {
		switch(action) {
		case DEVICE_REGISTER_ACTION:
			return processRegister(payload);
		}
		throw new ServerException("Not implemented");
	}

	protected DeviceRegistrationResponseDTO processRegister (String payload) throws ServerException {
		try {
			DeviceRegistrationRequestDTO request = mapper.readValue(payload, DeviceRegistrationRequestDTO.class);
			if (!request.getPIN().equals(model.getPIN())) {
				throw new ServerException("PIN validation failed");
			}
			// need to check if the device is already registered and if the type is the same
			Device device = deviceService.fetchByDeviceId(request.getDeviceId());
			if (device == null) {
				device = new Device();
				device.setDeviceId(request.getDeviceId());
				device.setDeviceType(DeviceTypeType.fromValue(request.getDeviceType()));
				device.setDeviceIp(request.getDeviceIp());
				device.setDevicePort(request.getDevicePort());
				device = deviceService.create(device);
			} else {
				if (!device.getDeviceType().equals(request.getDeviceType())) {
					device.setDeviceType(DeviceTypeType.fromValue(request.getDeviceType()));
				}
				device.setDeviceIp(request.getDeviceIp());
				device.setDevicePort(request.getDevicePort());
				device = deviceService.update(device);
			}
			DeviceRegistrationResponseDTO response = new DeviceRegistrationResponseDTO();
			response.setDeviceNumber(String.valueOf(device.getId()));
			return response;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to process message: " + ex.getMessage(), ex);
		} catch (ServiceException ex) {
			throw new ServerException("Failed to register device: " + ex.getMessage());
		}
	}
}
