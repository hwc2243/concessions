package com.concessions.client.service;

import com.concessions.client.service.base.BaseJournalService;
import com.concessions.dto.StatusType;

import java.util.List;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;

public interface JournalService extends BaseJournalService<Journal,Long>
{
	public Journal addOrder (Journal journal, Order order) throws ServiceException;
	
	public Journal recalcJournal (Journal journal) throws ServiceException;
	
	public List<Journal> findNotClosedJournals () throws ServiceException;
	
	public List<Journal> findAllByStatus (StatusType type) throws ServiceException;
	
	public Journal findByStatus (StatusType type) throws ServiceException;
	
	public Journal newInstance () throws ServiceException;
}