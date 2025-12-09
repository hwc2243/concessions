package com.concessions.api.external.base;

import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Journal;

import com.concessions.service.JournalService;
import com.concessions.service.ServiceException;

import com.concessions.api.external.base.BaseExternalJournalRest;

public abstract class BaseExternalJournalRestImpl implements BaseExternalJournalRest
{
	@Autowired
	protected JournalService journalService;

	@PostMapping
	@Override
	public ResponseEntity<Journal> createJournal (@RequestBody Journal journal)
	{
	  Journal newJournal = null;
	  
	  try
	  {
	    newJournal = journalService.create(journal);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }	
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newJournal);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Journal> deleteJournal(@PathVariable String id) 
    {
      try
      {
        journalService.delete(id);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.noContent().build();
    }

	@GetMapping("/{id}")
	@Override
	public ResponseEntity<Journal> getJournal (@PathVariable String id)
	{
		Journal journal = null;
		
		try	{
			journal = journalService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (journal != null) {
            return ResponseEntity.ok(journal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Journal>> listJournals ()
    {
        try
        {
        	List<Journal> journals = journalService.findAll();
        	return ResponseEntity.ok(journals);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Journal>> searchJournals (
	)
	{
	  try
	  {
	    List<Journal> allJournals = journalService.findAll();
	    List<Journal> filteredJournals = allJournals.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredJournals);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Journal> updateJournal(@PathVariable String id, @RequestBody Journal journal)
    {
      Journal updatedJournal = null;

      if (journal.getId() == null || "".equals(journal.getId()))
      {
      	journal.setId(id);
      }
      else if (!journal.getId().equals(id))
      {
        return ResponseEntity.badRequest().build();
      }
      try
      {
        Journal existingJournal = journalService.get(id);
        if (existingJournal == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedJournal = journalService.update(journal);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedJournal);
    }
}