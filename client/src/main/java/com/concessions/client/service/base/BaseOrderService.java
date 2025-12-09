package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseOrder;

public interface BaseOrderService<T extends BaseOrder, ID> extends EntityService<T, ID> {

	public List<T> findByJournalId (String journalId);
}