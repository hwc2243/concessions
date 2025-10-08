package com.concessions.service.base;

import java.util.List;

import com.concessions.model.base.BaseUser;

public interface BaseUserService<T extends BaseUser, ID> extends EntityService<T, ID> {

	public T fetchByUsername (String username);
}