package com.concessions.api.external.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Order;

import com.concessions.api.external.base.BaseExternalOrderRest;

public interface BaseExternalOrderRest
{
	public ResponseEntity<Order> createOrder (Order order);
	
    public ResponseEntity<Order> deleteOrder(String id); 

	public ResponseEntity<Order> getOrder (String id);
	
	public ResponseEntity<List<Order>> listOrders ();
	
	public ResponseEntity<List<Order>> searchOrders (
	);
	
    public ResponseEntity<Order> updateOrder (String id, Order order);
}