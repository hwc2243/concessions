package com.concessions.client.repository;

import com.concessions.client.model.Journal;

import com.concessions.client.repository.base.BaseJournalPersistence;
import com.concessions.client.model.StatusType;

import java.util.List;

public interface JournalPersistence extends BaseJournalPersistence<Journal,Long>
{
    List<Journal> findByEndTsIsNullAndOrganizationId(Long organizationId);
    
    List<Journal> findByStatusAndOrganizationId(StatusType status, Long organizationId);
    
    List<Journal> findByStatusNotAndOrganizationId(StatusType status, Long organizationId);
} 