package com.concessions.service;

import com.concessions.service.base.BaseUserService;

import com.concessions.model.User;

public interface UserService extends BaseUserService<User,Long>
{
	public User fullyFetchByUsername (String username) throws ServiceException;
}