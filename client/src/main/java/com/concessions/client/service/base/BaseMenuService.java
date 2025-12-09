package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseMenu;

public interface BaseMenuService<T extends BaseMenu, ID> extends EntityService<T, ID> {

	public List<T> findByName (String name);
}