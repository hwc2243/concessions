package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseOrderItem;
import com.concessions.model.OrderItem;

public interface BaseOrderItemPersistence<T extends OrderItem, ID> extends JpaRepository<T, ID>
{
} 