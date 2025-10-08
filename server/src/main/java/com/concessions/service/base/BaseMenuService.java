package com.concessions.service.base;

import java.util.List;

import com.concessions.model.base.BaseMenu;

public interface BaseMenuService<T extends BaseMenu, ID> extends EntityService<T, ID> {

	public List<T> findByName (String name);
}