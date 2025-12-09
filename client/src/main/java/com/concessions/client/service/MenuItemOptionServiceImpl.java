package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseMenuItemOptionServiceImpl;

import com.concessions.client.model.MenuItemOption;

@Service
public class MenuItemOptionServiceImpl
  extends BaseMenuItemOptionServiceImpl<MenuItemOption,Long>
  implements MenuItemOptionService
{
}