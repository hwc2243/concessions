package com.concessions.local.ui.controller;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.rest.MenuRestClient;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.view.OrderPanel;

import com.concessions.model.CategoryType;
import com.concessions.model.Menu;
import com.concessions.model.MenuItem;

import jakarta.annotation.PostConstruct;

@Component
public class OrderController {
	

	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected ApplicationFrame applicationFrame;
	
	private OrderPanel orderPanel;
	
	public OrderController() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	protected void initialize ()
	{
	}
	
	public void execute ()
	{
		Menu menu = applicationModel.getMenu();
		Map<CategoryType, List<MenuItem>> menuData = menu.getMenuItems().stream()
	            .collect(Collectors.groupingBy(MenuItem::getCategory));
		
		OrderModel orderModel = new OrderModel();
		orderModel.setMenuData(menuData);
		orderPanel = new OrderPanel(orderModel);
		applicationFrame.setMainContent(orderPanel);
	}
	
}
