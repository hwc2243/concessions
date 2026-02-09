package com.concessions.service.base;

import java.util.List;

import com.concessions.model.base.BaseJournal;
import com.concessions.dto.StatusType;

public interface BaseJournalService<T extends BaseJournal, ID> extends EntityService<T, ID> {
}