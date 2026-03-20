package com.concessions.local.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.pos.controller.OrderController;
import com.concessions.local.service.DynamicBeanService;

@Component
public class ClientOperationsState implements ApplicationState {

	@Autowired
	private ApplicationConfiguration appConfig;
	
	@Autowired
    private DynamicBeanService beanService;

	protected OrderController orderController;
	
	@Override
	public boolean isComplete() {
		return (orderController != null);
	}

	@Override
	public void execute() {
		orderController = beanService.createBean("orderController", OrderController.class);
	}
}
