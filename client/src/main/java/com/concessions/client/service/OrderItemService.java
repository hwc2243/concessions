package com.concessions.client.service;

import com.concessions.client.service.base.BaseOrderItemService;

import com.concessions.client.model.OrderItem;

public interface OrderItemService extends BaseOrderItemService<OrderItem,Long>
{
	public OrderItem newInstance ();
}