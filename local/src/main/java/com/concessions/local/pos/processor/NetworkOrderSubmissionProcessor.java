package com.concessions.local.pos.processor;

import com.concessions.common.network.Messenger;
import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.OrderRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.pos.model.POSApplicationModel;

public class NetworkOrderSubmissionProcessor implements OrderSubmissionProcessor {

	protected POSApplicationModel model;
	protected Messenger messenger;
	
	public NetworkOrderSubmissionProcessor(POSApplicationModel model, Messenger messenger) {
		this.model = model;
		this.messenger = messenger;
	}

	@Override
	public void submitOrder (OrderDTO order) {
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
