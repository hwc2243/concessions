package com.concessions.local.pos.controller;

import com.concessions.common.network.Messenger;
import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.pos.model.POSApplicationModel;
import com.concessions.local.ui.controller.OrderController.OrderListener;

public class OrderSubmissionController implements OrderListener {

	protected Messenger messenger;
	
	protected POSApplicationModel model;
	
	public OrderSubmissionController (POSApplicationModel model, Messenger clientService) {
		this.model = model;
		this.messenger = clientService;
	}

	@Override
	public void onOrderCreated (OrderDTO order) {
		OrderRequestDTO request = new OrderRequestDTO();
		request.setPIN(model.getPin());
		request.setDeviceId(model.getDeviceId());
		request.setOrder(order);
		try {
			messenger.sendRequest(NetworkConstants.ORDER_SERVICE, NetworkConstants.ORDER_SUBMIT_ACTION, request, SimpleResponseDTO.class);
		} catch (MessengerException ex) {
			ex.printStackTrace();
		}
	}
}
