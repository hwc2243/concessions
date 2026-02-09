package com.concessions.local.network.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.concessions.client.service.ServiceException;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.OrderQueueGetAllResponseDTO;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.dto.JournalDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.dto.DeviceDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.server.orchestrator.OrderException;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.ui.controller.JournalController;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class OrderHandler extends AbstractDeviceHandler {
	
	@Autowired
	@Lazy
	protected OrderOrchestrator orderOrchestrator;
	
	@Autowired
	protected JournalController journalController;
	
	public OrderHandler() {
	}

	@Override
	public String getName() {
		return ORDER_SERVICE;
	}

	@Override
	public Object process (String action, String payload) throws ServerException {
		switch (action) {
		case ORDER_SUBMIT_ACTION:
			return processSubmit(payload);
		case ORDER_GETALL_ACTION:
			return processGetAll(payload);
		case ORDER_COMPLETE_ACTION:
			return processComplete(payload);
		}
		throw new ServerException("Not implemented");
	}
	
	protected SimpleResponseDTO processSubmit (String payload) throws ServerException {
		try {
			OrderRequestDTO request = mapper.readValue(payload, OrderRequestDTO.class);
			validatePIN(request);
			validateDevice(request);
			// HWC TODO this should throw an exception if order submission failed and we should return an error to the client
			JournalDTO journal = orderOrchestrator.submitOrder(request.getOrder());
			
			// this is kind of kludgy just provide notifications that the journal changed
			model.setJournal(journal);
			journalController.change(model.getJournal());
			return success;
			
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to submit order: " + ex.getMessage(), ex);
		}
	}
	
	protected OrderQueueGetAllResponseDTO processGetAll (String payload) throws ServerException {
		try {
			SimpleDeviceRequestDTO request = mapper.readValue(payload, SimpleDeviceRequestDTO.class);
			validatePIN(request);
			validateDevice(request, DeviceTypeType.KITCHEN);
			DeviceDTO device = this.loadDevice(request.getDeviceId());
			List<OrderDTO> orders = orderOrchestrator.fetchOrders(device);
			OrderQueueGetAllResponseDTO response = new OrderQueueGetAllResponseDTO();
			response.setOrders(orders);
			return response;
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to retrieve orders: " + ex.getMessage(), ex);
		}
	}
	
	protected SimpleResponseDTO processComplete (String payload) throws ServerException {
		try {
			OrderRequestDTO request = mapper.readValue(payload, OrderRequestDTO.class);
			validatePIN(request);
			validateDevice(request, DeviceTypeType.KITCHEN);
			orderOrchestrator.completeOrder(request.getOrder());
		} catch (JsonProcessingException ex) {
			throw new ServerException("Failed to complete order: " + ex.getMessage(), ex);
		} catch (OrderException ex) {
			throw new ServerException("Failed to complete order: " + ex.getMessage(), ex);
		}
		return success;
	}
}
