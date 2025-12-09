package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalJournalRestImpl;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.model.Journal;
import com.concessions.model.Order;
import com.concessions.service.OrderService;
import com.concessions.service.ReconciliationException;
import com.concessions.service.ServiceException;

import java.util.List;

@RestController
@RequestMapping("/api/external/journal")
public class ExternalJournalRestImpl
   extends BaseExternalJournalRestImpl
   implements ExternalJournalRest
{
	@Autowired
	protected OrderService orderService;
	
	@PatchMapping("/{id}/syncOrders")
	@Override
	public ResponseEntity<Void> syncOrders (@PathVariable("id") String journalId, @RequestBody List<Order> orders)
	{
	  
	  try
	  {
		  Journal journal = journalService.get(journalId);
		  if (journal == null) {
			  return ResponseEntity.notFound().build();
		  }
		  for (Order order : orders) {
			  if (!journalId.equals(order.getJournalId())) {
				  return ResponseEntity.badRequest().build();
			  }
			  orderService.create(order);
		  }
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }	
	  
	  return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{journalId}/reconcile")
	@Override
    public ResponseEntity<Journal> reconcileJournal(@PathVariable("journalId") String journalId, @RequestBody JournalSummaryDTO summary)
    {
        try {
        	Journal journal = journalService.get(journalId);

            journal = journalService.reconcile(journal, summary); // Example service call
            
            return ResponseEntity.ok(journal);
        } catch (ReconciliationException ex) {
        	return ResponseEntity.badRequest().build();
        } catch (ServiceException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}