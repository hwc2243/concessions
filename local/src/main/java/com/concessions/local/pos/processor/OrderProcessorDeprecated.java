package com.concessions.local.pos.processor;

import java.util.List;

import com.concessions.dto.OrderDTO;
import com.concessions.local.server.orchestrator.OrderException;

public interface OrderProcessorDeprecated {
	public void completeOrder (OrderDTO order) throws OrderException;
	
	public List<OrderDTO> getOrders () throws OrderException;
	
	public void submitOrder (OrderDTO order) throws OrderException;
	
}
