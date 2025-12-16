package com.concessions.local.pos.controller;

import com.concessions.local.network.client.ClientException;
import com.concessions.local.network.client.ClientService;
import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.OrderDTO;
import com.concessions.local.network.dto.OrderRequestDTO;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.network.manager.OrderManager;
import com.concessions.local.pos.model.POSApplicationModel;
import com.concessions.local.ui.controller.OrderController.OrderListener;

public class OrderSubmissionController implements OrderListener {

	protected ClientService clientService;
	
	protected POSApplicationModel model;
	
	public OrderSubmissionController (POSApplicationModel model, ClientService clientService) {
		this.model = model;
		this.clientService = clientService;
	}

	@Override
	public void onOrderCreated (OrderDTO order) {
		OrderRequestDTO request = new OrderRequestDTO();
		request.setPIN(model.getPin());
		request.setDeviceId(model.getDeviceId());
		request.setOrder(order);
		try {
			clientService.sendRequest(OrderManager.NAME, OrderManager.SUBMIT, request, SimpleResponseDTO.class);
		} catch (ClientException ex) {
			ex.printStackTrace();
		}
	}
}
