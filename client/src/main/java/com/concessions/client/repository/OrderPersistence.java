package com.concessions.client.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.concessions.client.model.Order;

import com.concessions.client.repository.base.BaseOrderPersistence;

public interface OrderPersistence extends BaseOrderPersistence<Order,Long>
{

    List<Order> findByJournalIdAndEndTsIsNullOrderByStartTsAsc (String journalId);
} 