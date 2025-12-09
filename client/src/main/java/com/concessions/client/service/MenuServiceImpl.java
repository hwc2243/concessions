package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseMenuServiceImpl;

import jakarta.transaction.Transactional;

import com.concessions.client.model.Menu;

@Service
public class MenuServiceImpl
  extends BaseMenuServiceImpl<Menu,Long>
  implements MenuService
{
	@Override
	@Transactional
	public Menu get (Long id) throws ServiceException
	{
		Menu menu = super.get(id);
		menu.getMenuItems().size();
		return menu;
	}
}