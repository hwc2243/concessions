package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseOrderItem;
import com.concessions.client.model.OrderItem;

public interface BaseOrderItemPersistence<T extends OrderItem, ID> extends JpaRepository<T, ID>
{
} 