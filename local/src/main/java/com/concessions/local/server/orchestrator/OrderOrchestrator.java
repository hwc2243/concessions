package com.concessions.local.server.orchestrator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;
import com.concessions.client.service.JournalService;
import com.concessions.client.service.OrderService;
import com.concessions.client.service.ServiceException;
import com.concessions.common.util.UniqueQueue;
import com.concessions.dto.JournalDTO;
import com.concessions.dto.OrderDTO;
import com.concessions.local.dto.JournalMapper;
import com.concessions.local.dto.OrderMapper;
import com.concessions.local.util.MoneyUtil;

@Component
public class OrderOrchestrator {
	private static final Logger logger = LoggerFactory.getLogger(OrderOrchestrator.class);
	
	protected JournalService journalService;
	protected OrderService orderService;
	
	protected JournalDTO journal;
	
	protected UniqueQueue<OrderDTO> orderQueue = new UniqueQueue<>();
	
	public OrderOrchestrator(@Autowired JournalService journalService, @Autowired OrderService orderService) {
		this.journalService = journalService;
		this.orderService = orderService;
	}

	public void initialize (JournalDTO journal) {
		this.journal = journal;
		
		List<Order> openOrders = orderService.findOpen(journal.getId());
		openOrders.stream()
				.map(OrderMapper::toDto)
				.forEach(this::queueOrder);
	}
	
	protected void queueOrder (OrderDTO order) {
		logger.info("Queuing order: {}", order.getId());
		
		if (orderQueue.add(order)) {
			logger.info("Notifying kitchen of new order");
		}
	}
	
	public JournalDTO submitOrder (OrderDTO order) {
		logger.info("Received a new order for {}.", MoneyUtil.formatAsMoney(order.getOrderTotal()));
		
		// HWC TODO this should be handled better and an error should be returned to the call
		if (!journal.getId().equals(order.getJournalId())) {
			throw new RuntimeException("Current journal and order journal do not match");
		}
        Order orderEntity = OrderMapper.fromDto(order);
        try {
        	orderEntity = orderService.create(orderEntity);
        	order = OrderMapper.toDto(orderEntity);
        } catch (ServiceException ex) {
        	ex.printStackTrace();
        }
        
		try {
			Journal journalEntity = journalService.addOrder(JournalMapper.fromDto(journal), orderEntity);
			queueOrder(order);
			return JournalMapper.toDto(journalEntity);
		} catch (ServiceException ex) {
			// HWC TODO this should be a cancelable event and this should cancel it
			ex.printStackTrace();
		}
		return null;
	}
}
