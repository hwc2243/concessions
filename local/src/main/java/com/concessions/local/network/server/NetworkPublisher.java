package com.concessions.local.network.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.common.network.Messenger;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.model.Device;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.server.orchestrator.OrderOrchestrator.OrderListener;
import com.concessions.local.service.DeviceService;

@Component
public class NetworkPublisher implements NetworkConstants, OrderListener {

	protected DeviceService deviceService;
	protected Messenger messenger;
	
	public NetworkPublisher(@Autowired DeviceService deviceService, @Autowired Messenger messenger, @Autowired OrderOrchestrator orderOrchestrator) {
		this.deviceService = deviceService;
		this.messenger = messenger;
		orderOrchestrator.addOrderListener(this);
	}

	@Override
	public void orderCompleted(OrderDTO order) {
		List<Device> kitchenDevices = findKitchenDevices();
		kitchenDevices.stream().forEach(device -> {
			try {
				messenger.sendRequest(device.getDeviceIp(), device.getDevicePort(), ORDER_SERVICE, ORDER_COMPLETED_ACTION, order, SimpleResponseDTO.class);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@Override
	public void orderCreated(OrderDTO order) {
		List<Device> kitchenDevices = findKitchenDevices();
		kitchenDevices.stream().forEach(device -> {
			if (StringUtils.isNotBlank(device.getDeviceIp()) && device.getDevicePort() > 0) {
				try {
					messenger.sendRequest(device.getDeviceIp(), device.getDevicePort(), ORDER_SERVICE, ORDER_CREATED_ACTION, order, SimpleResponseDTO.class);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected List<Device> findKitchenDevices () {
		return deviceService.findByDeviceType(DeviceTypeType.KITCHEN);
	}
}
