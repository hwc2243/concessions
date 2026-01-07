package com.concessions.service.base;

import java.util.List;

import com.concessions.model.base.BaseMenuItem;
import com.concessions.model.CategoryType;

public interface BaseMenuItemService<T extends BaseMenuItem, ID> extends EntityService<T, ID> {
}