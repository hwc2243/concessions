package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseMenuItemServiceImpl;

import com.concessions.model.MenuItem;

@Service
public class MenuItemServiceImpl
  extends BaseMenuItemServiceImpl<MenuItem,Long>
  implements MenuItemService
{
}