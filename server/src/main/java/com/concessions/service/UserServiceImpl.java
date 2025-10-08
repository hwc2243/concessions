package com.concessions.service;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concessions.service.base.BaseUserServiceImpl;

import com.concessions.model.User;

@Service
public class UserServiceImpl
  extends BaseUserServiceImpl<User,Long>
  implements UserService
{

	@Override
	@Transactional(readOnly=true)
	public User fullyFetchByUsername (String username) throws ServiceException {
		User user = this.fetchByUsername(username);
		
		if (user != null) {
			Hibernate.initialize(user.getOrganizations());
			Hibernate.initialize(user.getLocations());
		}
		
		return user;
		
	}
}