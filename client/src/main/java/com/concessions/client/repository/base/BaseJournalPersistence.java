package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseJournal;
import com.concessions.client.model.Journal;
import com.concessions.client.model.StatusType;

public interface BaseJournalPersistence<T extends Journal, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	
} 