package com.concessions.client.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseJournalServiceImpl;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.dto.StatusType;

import jakarta.transaction.Transactional;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;

@Service
public class JournalServiceImpl
  extends BaseJournalServiceImpl<Journal,Long>
  implements JournalService
{
	protected Collection<StatusType> notClosed = List.of(StatusType.CLOSE, StatusType.SYNC);
	
	@Autowired
	protected OrderService orderService;
	
	@Override
	public List<Journal> findNotClosedJournals() throws ServiceException {
		return journalPersistence.findByStatusNotInAndOrganizationId(notClosed, tenantDiscriminator.getOrganizationId());
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
	public synchronized Journal addOrder (Journal journal, Order order) throws ServiceException {
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
		return update(journal);
	}
	
	@Override
	public Journal recalcJournal (Journal journal) throws ServiceException {
		List<Order> orders = orderService.findByJournalId(journal.getId());
		if (orders != null && !orders.isEmpty()) {
			JournalSummaryDTO calcdSummary = orders.stream()
				    .collect(
				        // Supplier: Create a new OrderSummary object (The initial container)
				        JournalSummaryDTO::new, 
				        
				        // Accumulator: Add one Order's data to the container
				        (s, order) -> { 
				            s.incrementCount();
				            s.addToTotal(order.getOrderTotal());
				        }, 
				        
				        // Combiner: Merge two containers (Used if running in parallel)
				        JournalSummaryDTO::merge
				    );
			journal.setOrderCount(calcdSummary.getOrderCount());
			journal.setSalesTotal(calcdSummary.getSalesTotal());
		} else {
			journal.setOrderCount(0L);
			journal.setSalesTotal(BigDecimal.ZERO);
		}
		
		return journal;
	}
}