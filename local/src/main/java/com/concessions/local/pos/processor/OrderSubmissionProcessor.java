package com.concessions.local.pos.processor;

import com.concessions.dto.OrderDTO;

public interface OrderSubmissionProcessor {
	public void submitOrder (OrderDTO order);
}
