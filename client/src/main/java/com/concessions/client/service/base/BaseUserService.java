package com.concessions.client.service.base;

import java.util.List;

import com.concessions.client.model.base.BaseUser;

public interface BaseUserService<T extends BaseUser, ID> extends EntityService<T, ID> {

	public T fetchByUsername (String username);
}