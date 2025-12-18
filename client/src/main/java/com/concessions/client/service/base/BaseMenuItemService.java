package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseMenuItem;
import com.concessions.client.model.CategoryType;

public interface BaseMenuItemService<T extends BaseMenuItem, ID> extends EntityService<T, ID> {
}