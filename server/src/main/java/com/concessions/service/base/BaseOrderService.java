package com.concessions.service.base;

import java.util.List;

import com.concessions.model.base.BaseOrder;

public interface BaseOrderService<T extends BaseOrder, ID> extends EntityService<T, ID> {

	public List<T> findByJournalId (String journalId);
}