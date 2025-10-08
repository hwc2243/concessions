package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseMenuServiceImpl;

import com.concessions.model.Menu;

@Service
public class MenuServiceImpl
  extends BaseMenuServiceImpl<Menu,Long>
  implements MenuService
{
}