package com.concessions.client.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseOrderItemServiceImpl;

import com.concessions.client.model.OrderItem;

@Service
public class OrderItemServiceImpl
  extends BaseOrderItemServiceImpl<OrderItem,Long>
  implements OrderItemService
{

	@Override
	public OrderItem newInstance() {
		OrderItem orderItem = new OrderItem();
		orderItem.setId(UUID.randomUUID().toString());

		return orderItem;
	}
}