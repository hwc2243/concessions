package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseMenuItemOptionServiceImpl;

import com.concessions.model.MenuItemOption;

@Service
public class MenuItemOptionServiceImpl
  extends BaseMenuItemOptionServiceImpl<MenuItemOption,Long>
  implements MenuItemOptionService
{
}