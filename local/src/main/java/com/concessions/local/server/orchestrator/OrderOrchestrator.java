package com.concessions.local.server.orchestrator;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.dto.DeviceDTO;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.dto.JournalMapper;
import com.concessions.local.dto.OrderMapper;
import com.concessions.local.model.Device;
import com.concessions.local.service.DeviceService;
import com.concessions.local.util.MoneyUtil;

import jakarta.annotation.PostConstruct;

@Component
public class OrderOrchestrator {
	private static final Logger logger = LoggerFactory.getLogger(OrderOrchestrator.class);

	@Autowired
	protected ApplicationConfiguration appConfig;

	protected DeviceService deviceService;
	protected JournalService journalService;
	protected OrderService orderService;

	protected JournalDTO journal;

	protected UniqueQueue<OrderDTO> orderQueue = new UniqueQueue<>();

	protected List<OrderListener> listeners = new ArrayList<>();

	public OrderOrchestrator(@Autowired DeviceService deviceService, @Autowired JournalService journalService,
			@Autowired OrderService orderService) {
		this.deviceService = deviceService;
		this.journalService = journalService;
		this.orderService = orderService;
	}

	@PostConstruct
	public void initialize() {
		appConfig.addPropertyChangeListener(evt -> {
			if (evt.getSource() == appConfig && ApplicationConfiguration.PROPERTY_JOURNAL.equals(evt.getPropertyName())) {
				this.journal = (JournalDTO)evt.getNewValue();
				loadOrders(journal);
			}
		});
		journal = appConfig.getJournal();
		if (journal != null) {
			loadOrders(journal);
		}
	}
	
	private void loadOrders (JournalDTO journal) {
		if (journal != null) {
			List<Order> openOrders = orderService.findOpen(journal.getId());
			openOrders.stream().map(OrderMapper::toDto).forEach(this::queueOrder);
		}
	}

	public void completeOrder (OrderDTO dto) throws OrderException {
		Order order = OrderMapper.fromDto(dto);
		order.setEndTs(LocalDateTime.now());
		try {
			orderService.update(order);
		} catch (ServiceException ex) {
			throw new OrderException(ex);
		}

		orderQueue.remove(dto);
		notifyOrderCompleted(dto);
	}

	public List<OrderDTO> fetchOrders(DeviceDTO device) {
		if (device.getDeviceType() != DeviceTypeType.KITCHEN) {
			throw new RuntimeException("Invalid device type to retrieve orders");
		}

		return orderQueue.getAll();
	}

	protected void queueOrder(OrderDTO order) {
		logger.info("Queuing order: {}", order.getId());

		if (orderQueue.add(order)) {
			logger.info("Notifying kitchen of new order");
			notifyOrderCreated(order);
			List<Device> devices = deviceService.findByDeviceType(DeviceTypeType.KITCHEN);
			devices.stream().forEach(device -> {

			});
		}
	}

	public JournalDTO submitOrder (OrderDTO order) {
		logger.info("Received a new order for {}.", MoneyUtil.formatAsMoney(order.getOrderTotal()));

		// HWC TODO this should be handled better and an error should be returned to the
		// call
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

	public void addOrderListener(OrderListener listener) {
		listeners.add(listener);
	}

	public void removeOrderListener(OrderListener listener) {
		listeners.remove(listener);
	}

	protected void notifyOrderCreated(OrderDTO order) {
		listeners.stream().forEach(listener -> listener.orderCreated(order));
	}

	protected void notifyOrderCompleted(OrderDTO order) {
		listeners.stream().forEach(listener -> listener.orderCompleted(order));
	}

	public interface OrderListener {
		public void orderCompleted(OrderDTO order);

		public void orderCreated(OrderDTO order);
	}
}
