package com.concessions.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseJournalServiceImpl;
import com.concessions.model.Order;
import com.concessions.model.StatusType;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.model.Journal;

@Service
public class JournalServiceImpl
  extends BaseJournalServiceImpl<Journal,String>
  implements JournalService
{

	@Autowired
	protected OrderService orderService;
	
	@Override
	public Journal reconcile (Journal journal, JournalSummaryDTO summary) throws ServiceException {
		List<Order> orders = orderService.findByJournalId(journal.getId());
		JournalSummaryDTO storedSummary = new JournalSummaryDTO(journal.getOrderCount(), journal.getSalesTotal());
		// HWC TODO should this be done in parallel, everything is setup to support it
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
		
		if (calcdSummary.equals(summary) && storedSummary.equals(summary)) {
			journal.setSyncTs(LocalDateTime.now());
			journal.setStatus(StatusType.SYNC);
			return update(journal);
			
		} else {
			throw new ReconciliationException();
		}
	}
}