package com.concessions.local.pos.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.OrderQueueGetAllResponseDTO;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.base.model.AbstractClientModel;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.network.Messenger;
import com.concessions.local.server.orchestrator.OrderException;

public class OrderProcessor {

	private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);

	protected ApplicationConfiguration appConfig;
	
	protected Messenger messenger;

	public OrderProcessor(Messenger messenger, ApplicationConfiguration appConfig) {
		this.messenger = messenger;
		this.appConfig = appConfig;
	}

	public void completeOrder(OrderDTO order) throws OrderException {

		if (order != null) {
			logger.info("Completing order: {}", order.getId());
			OrderRequestDTO request = new OrderRequestDTO();
			request.setPIN(appConfig.getPin());
			request.setDeviceId(appConfig.getDeviceId());
			request.setOrder(order);
			try {
				SimpleResponseDTO response = messenger.sendRequest(NetworkConstants.ORDER_SERVICE,
						NetworkConstants.ORDER_COMPLETE_ACTION, request, SimpleResponseDTO.class);
			} catch (MessengerException ex) {
				logger.error("Failed to complete order - " + ex.getMessage(), ex);
				throw new OrderException(ex);
			}
		} else {
			logger.warn("Attempted to complete a null order.");
		}
	}

	public void submitOrder(OrderDTO order) throws OrderException {

		if (order != null) {
			OrderRequestDTO request = new OrderRequestDTO();
			request.setPIN(appConfig.getPin());
			request.setDeviceId(appConfig.getDeviceId());
			request.setOrder(order);
			try {
				messenger.sendRequest(NetworkConstants.ORDER_SERVICE, NetworkConstants.ORDER_SUBMIT_ACTION, request,
						SimpleResponseDTO.class);
			} catch (MessengerException ex) {
				logger.error("Failed to submit order - " + ex.getMessage(), ex);
				throw new OrderException(ex);
			}
		} else {
			logger.warn("Attempted to submit a null order.");
		}
	}

	public List<OrderDTO> getOrders() throws OrderException {
		try {
			SimpleDeviceRequestDTO request = new SimpleDeviceRequestDTO();
			request.setPIN(appConfig.getPin());
			request.setDeviceId(appConfig.getDeviceId());
			OrderQueueGetAllResponseDTO response = messenger.sendRequest(NetworkConstants.ORDER_SERVICE,
					NetworkConstants.ORDER_GETALL_ACTION, request, OrderQueueGetAllResponseDTO.class);
			return response.getOrders();

		} catch (MessengerException ex) {
			logger.error("Failed to retrieve orders - " + ex.getMessage(), ex);
			throw new OrderException(ex);
		}
	}
}
