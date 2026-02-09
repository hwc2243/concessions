package com.concessions.local.kitchen.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.concessions.common.network.AbstractHandler;
import com.concessions.common.network.NetworkException;
import com.concessions.dto.OrderDTO;
import com.concessions.local.kitchen.model.OrderDisplayModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(
	    value = "local.network.client", 
	    havingValue = "true", 
	    matchIfMissing = false
	)
public class OrderClientHandler extends AbstractHandler {

	protected OrderDisplayModel orderDisplayModel;
	
	public OrderClientHandler (@Autowired ObjectMapper mapper, @Autowired OrderDisplayModel orderDisplayModel) {
		super(mapper);
		this.orderDisplayModel = orderDisplayModel;
	}

	@Override
	public String getName() {
		return ORDER_SERVICE;
	}

	@Override
	public Object process(String action, String payload) throws NetworkException {
		switch (action) {
		case ORDER_COMPLETED_ACTION:
			processOrderCompleted(payload);
			break;
		case ORDER_CREATED_ACTION:
			processOrderCreated(payload);
			break;
		}
		return success;
	}

	protected void processOrderCompleted (String payload) {
		try {
			OrderDTO order = mapper.readValue(payload, OrderDTO.class);
			orderDisplayModel.removeOrder(order);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}
	
	protected void processOrderCreated (String payload) {
		try {
			OrderDTO order = mapper.readValue(payload, OrderDTO.class);
			orderDisplayModel.addOrder(order);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}
}
