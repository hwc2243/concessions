package com.concessions.client.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.model.base.BaseOrder;
import com.concessions.client.model.Order;

import com.concessions.client.service.ServiceException;

import com.concessions.client.repository.OrderPersistence;
import com.concessions.client.repository.base.BaseOrderPersistence;

public abstract class BaseOrderServiceImpl<T extends Order, ID>
  implements BaseOrderService<T, ID> {

  @Autowired
  private BaseOrderPersistence<T,ID> baseOrderPersistence;
  
  @Autowired
  protected OrderPersistence orderPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseOrderPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseOrderPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseOrderPersistence.findAll();
  }

  @Override
  public List<T> findByJournalId (String journalId)
  {
	return baseOrderPersistence.findByJournalId(journalId);
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseOrderPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseOrderPersistence.save(entity);
  }
}