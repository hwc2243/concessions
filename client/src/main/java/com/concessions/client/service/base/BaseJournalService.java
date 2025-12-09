package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseJournal;

public interface BaseJournalService<T extends BaseJournal, ID> extends EntityService<T, ID> {
}