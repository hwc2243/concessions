package com.concessions.local.pos.processor;

import com.concessions.dto.OrderDTO;
import com.concessions.local.server.orchestrator.OrderOrchestrator;

public class LocalOrderSubmissionProcessor implements OrderSubmissionProcessor {

	protected OrderOrchestrator orderOrchestrator;
	
	public LocalOrderSubmissionProcessor(OrderOrchestrator orderOrchestrator) {
		this.orderOrchestrator = orderOrchestrator;
	}

	@Override
	public void submitOrder(OrderDTO order) {
		orderOrchestrator.submitOrder(order);
	}
}
