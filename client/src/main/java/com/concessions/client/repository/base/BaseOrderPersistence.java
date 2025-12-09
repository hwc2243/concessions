package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseOrder;
import com.concessions.client.model.Order;

public interface BaseOrderPersistence<T extends Order, ID> extends JpaRepository<T, ID>
{

    public List<T> findByJournalId (String journalId);
} 