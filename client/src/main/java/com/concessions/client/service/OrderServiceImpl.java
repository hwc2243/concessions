package com.concessions.client.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseOrderServiceImpl;
import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;

@Service
public class OrderServiceImpl
  extends BaseOrderServiceImpl<Order,Long>
  implements OrderService
{

	@Override
	public Order newInstance(Journal journal) {
		Order order = new Order();
		order.setId(UUID.randomUUID().toString());
		order.setJournalId(journal.getId());
		
		return order;
	}

	@Override
	public List<Order> findOpen (String journalId) {
		return this.orderPersistence.findByJournalIdAndEndTsIsNullOrderByStartTsAsc(journalId);
	}
}