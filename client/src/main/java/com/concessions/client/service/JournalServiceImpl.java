package com.concessions.client.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseJournalServiceImpl;
import com.concessions.client.model.StatusType;

import jakarta.transaction.Transactional;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;

@Service
public class JournalServiceImpl
  extends BaseJournalServiceImpl<Journal,Long>
  implements JournalService
{
	@Autowired
	protected OrderService orderService;
	
	@Override
	public List<Journal> findNotClosedJournals() throws ServiceException {
		return journalPersistence.findByStatusNotAndOrganizationId(StatusType.CLOSE, tenantDiscriminator.getOrganizationId());
	}

	@Override
	public List<Journal> findAllByStatus(StatusType type) throws ServiceException {
		return journalPersistence.findByStatusAndOrganizationId(type, tenantDiscriminator.getOrganizationId());
	}

	@Override
	public Journal findByStatus(StatusType type) throws ServiceException {
		List<Journal> journals = findAllByStatus(type);
		Journal journal = null;
		if (!journals.isEmpty()) {
			if (journals.size() == 1) {
				journal = journals.get(0);
			} else {
				throw new ServiceException("Multiple " + type + " journals were found when one was requested");
			}
		}
		return journal;
	}
	
	@Override
	public Journal newInstance() throws ServiceException {
		Journal journal = new Journal();
		journal.setId(UUID.randomUUID().toString());
		journal.setOrganizationId(tenantDiscriminator.getOrganizationId());
		journal.setStartTs(LocalDateTime.now());
		journal.setStatus(StatusType.NEW);
		journal.setOrderCount(0L);
		journal.setSalesTotal(BigDecimal.ZERO);
		
		return super.create(journal);
	}

	@Override
	@Transactional
	public synchronized void addOrder (Journal journal, Order order) throws ServiceException {
		if (order.getJournalId() != null && !"".equals(order.getJournalId())) {
			if (!order.getJournalId().equals(journal.getId())) {
				throw new ServiceException("Order is assigned to another journal already");
			}
		} else {
			order.setJournalId(journal.getId());
			orderService.update(order);
		}
		
		journal.setOrderCount(journal.getOrderCount() + 1);
		journal.setSalesTotal(journal.getSalesTotal().add(order.getOrderTotal()));
		update(journal);
	}
}