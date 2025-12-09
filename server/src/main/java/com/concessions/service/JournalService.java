package com.concessions.service;

import com.concessions.service.base.BaseJournalService;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.model.Journal;

public interface JournalService extends BaseJournalService<Journal,String>
{
	public Journal reconcile (Journal journal, JournalSummaryDTO summary) throws ServiceException;
}