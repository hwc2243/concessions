package com.concessions.client.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.model.base.BaseJournal;
import com.concessions.client.model.Journal;

import com.concessions.client.service.ServiceException;

import com.concessions.client.repository.JournalPersistence;
import com.concessions.client.repository.base.BaseJournalPersistence;

import com.concessions.client.model.base.Multitenant;
import com.concessions.client.service.base.MultitenantServiceImpl;
import com.concessions.dto.StatusType;

public abstract class BaseJournalServiceImpl<T extends Journal & Multitenant, ID>
  extends MultitenantServiceImpl<T, ID>
  implements BaseJournalService<T, ID> {

  @Autowired
  private BaseJournalPersistence<T,ID> baseJournalPersistence;
  
  @Autowired
  protected JournalPersistence journalPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for creating journal");
    }
    
    return baseJournalPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    try {
      get(id);
    } catch (ServiceException ex) {
      throw new ServiceException("Access denied for deleting journal with id = " + id);
    }
    
    baseJournalPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseJournalPersistence.findByOrganizationId(tenantDiscriminator.getOrganizationId());
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseJournalPersistence.findById(id);

    if (optional.isPresent()) {
      if (!hasAccess(optional.get())) {
		throw new ServiceException("Access denied for journal with id = " + id);
	  }
	}
    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for updating journal with id = " + entity.getId());
    }
    return baseJournalPersistence.save(entity);
  }
}