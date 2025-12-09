package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseUserServiceImpl;

import com.concessions.client.model.User;

@Service
public class UserServiceImpl
  extends BaseUserServiceImpl<User,Long>
  implements UserService
{
}