package com.concessions.client.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.concessions.client.model.OrderItem;

import com.concessions.client.repository.base.BaseOrderItemPersistence;

public interface OrderItemPersistence extends BaseOrderItemPersistence<OrderItem,Long>
{
} 