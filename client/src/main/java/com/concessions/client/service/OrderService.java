package com.concessions.client.service;

import com.concessions.client.service.base.BaseOrderService;

import java.util.List;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;

public interface OrderService extends BaseOrderService<Order,Long>
{
	public Order newInstance (Journal journal);
	
	public List<Order> findOpen (String journalId);
}