package com.concessions.local.pos.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.concessions.dto.OrderDTO;
import com.concessions.local.dto.DeviceDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.server.orchestrator.OrderException;
import com.concessions.local.server.orchestrator.OrderOrchestrator;

public class LocalOrderProcessor implements OrderProcessorDeprecated {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalOrderProcessor.class);

	protected OrderOrchestrator orderOrchestrator;
	
	protected DeviceDTO localDevice;
	
	public LocalOrderProcessor(OrderOrchestrator orderOrchestrator) {
		this.orderOrchestrator = orderOrchestrator;
		localDevice = new DeviceDTO();
		localDevice.setDeviceType(DeviceTypeType.KITCHEN);
	}

	@Override
	public void completeOrder (OrderDTO order) throws OrderException {
		orderOrchestrator.completeOrder(order);
	}
	
	@Override
	public void submitOrder (OrderDTO order) throws OrderException {
		orderOrchestrator.submitOrder(order);
	}

	@Override
	public List<OrderDTO> getOrders () throws OrderException {
		return orderOrchestrator.fetchOrders(localDevice);
	}
}
