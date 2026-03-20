package com.concessions.local.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Menu;
import com.concessions.client.service.MenuService;
import com.concessions.client.service.ServiceException;
import com.concessions.dto.MenuDTO;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.dto.MenuMapper;
import com.concessions.local.service.DynamicBeanService;
import com.concessions.local.ui.controller.JournalController;

@Component
public class ServerOperationsState implements ApplicationState {

	@Autowired
	private ApplicationConfiguration appConfig;
	
	@Autowired
    private DynamicBeanService beanService;
	
	@Autowired
	private MenuService menuService;
	
	protected boolean complete = false;
	
	protected JournalController journalController = null;
	
	@Override
	public boolean isComplete() {
		return (journalController != null);
	}

	@Override
	public void execute() {
		journalController = beanService.createBean("journalController", JournalController.class);
		if (appConfig.getLocationConfiguration() != null && appConfig.getLocationConfiguration().getMenuId() > -1) {
			try {
				Menu menu = menuService.get(appConfig.getLocationConfiguration().getMenuId());
				appConfig.setMenu(MenuMapper.toDto(menu));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
}
