package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseMenuItemServiceImpl;

import com.concessions.client.model.MenuItem;

@Service
public class MenuItemServiceImpl
  extends BaseMenuItemServiceImpl<MenuItem,Long>
  implements MenuItemService
{
}