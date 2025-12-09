package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseOrder;
import com.concessions.model.Order;

public interface BaseOrderPersistence<T extends Order, ID> extends JpaRepository<T, ID>
{

    public List<T> findByJournalId (String journalId);
} 