package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseJournal;
import com.concessions.client.model.StatusType;

public interface BaseJournalService<T extends BaseJournal, ID> extends EntityService<T, ID> {
}