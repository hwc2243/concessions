package com.concessions.local.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.client.service.JournalService;
import com.concessions.client.service.MenuItemOptionService;
import com.concessions.client.service.MenuItemService;
import com.concessions.client.service.MenuService;
import com.concessions.client.service.OrderItemService;
import com.concessions.client.service.OrderService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ResetService {

	private static final Logger logger = LoggerFactory.getLogger(ResetService.class);

	@Autowired
	protected EntityManager entityManager;

	@Autowired
	protected ApplicationConfigurationService appConfigService;

	@Autowired
	protected ClientConfigurationService clientConfigService;

	@Autowired
	protected ServerConfigurationService serverConfigService;

	@Autowired
	protected DeviceService deviceService;

	@Autowired
	protected JournalService journalService;

	@Autowired
	protected MenuService menuService;

	@Autowired
	protected MenuItemService menuItemService;

	@Autowired
	protected MenuItemOptionService menuItemOptionService;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected OrderItemService orderItemService;

	@Transactional
	public void reset() {
		orderItemService.deleteAll();
		orderService.deleteAll();
		menuItemOptionService.deleteAll();
		menuItemService.deleteAll();
		menuService.deleteAll();
		journalService.deleteAll();
		deviceService.deleteAll();
		entityManager.flush();

		try {
			appConfigService.reset();
			clientConfigService.reset();
			serverConfigService.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
