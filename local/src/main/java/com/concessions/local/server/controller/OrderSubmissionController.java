package com.concessions.local.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;
import com.concessions.client.service.JournalService;
import com.concessions.client.service.OrderService;
import com.concessions.client.service.ServiceException;
import com.concessions.dto.JournalDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.network.dto.JournalMapper;
import com.concessions.local.network.dto.OrderMapper;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.ui.controller.OrderController;
import com.concessions.local.ui.controller.OrderController.OrderListener;
import com.concessions.local.util.MoneyUtil;

import jakarta.annotation.PostConstruct;

@Component
public class OrderSubmissionController implements OrderListener {
	private static final Logger logger = LoggerFactory.getLogger(OrderSubmissionController.class);

	@Autowired
	protected JournalService journalService;
	
	@Autowired
	protected OrderService orderService;
	
	@Autowired
	protected OrderController orderController;
	
	@Autowired
	protected ServerApplicationModel model;
	
	
	public OrderSubmissionController() {
	}

	@PostConstruct
	protected void initialize () {
		orderController.addOrderListener(this);
	}
	
	@Override
	public void onOrderCreated (OrderDTO order) {
		logger.info("Received a new order for {}.", MoneyUtil.formatAsMoney(order.getOrderTotal()));
		JournalDTO journal = model.getJournal();
		
		// HWC TODO if the journal.id and the order.journalId are not the same error
        Order orderEntity = OrderMapper.fromDto(order);
        Order persistedOrderEntity = null;
        try {
        	persistedOrderEntity = orderService.create(orderEntity);
        } catch (ServiceException ex) {
        	ex.printStackTrace();
        }
        
		try {
			Journal journalEntity = journalService.addOrder(JournalMapper.fromDto(journal), persistedOrderEntity);
			model.setJournal(JournalMapper.toDto(journalEntity));
		} catch (ServiceException ex) {
			// HWC TODO this should be a cancelable event and this should cancel it
			ex.printStackTrace();
		}
	}
}
