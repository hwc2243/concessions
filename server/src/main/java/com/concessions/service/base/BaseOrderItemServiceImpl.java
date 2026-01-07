package com.concessions.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.model.base.BaseOrderItem;
import com.concessions.model.OrderItem;

import com.concessions.service.ServiceException;

import com.concessions.persistence.OrderItemPersistence;
import com.concessions.persistence.base.BaseOrderItemPersistence;


public abstract class BaseOrderItemServiceImpl<T extends OrderItem, ID>
  implements BaseOrderItemService<T, ID> {

  @Autowired
  private BaseOrderItemPersistence<T,ID> baseOrderItemPersistence;
  
  @Autowired
  protected OrderItemPersistence orderItemPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseOrderItemPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseOrderItemPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseOrderItemPersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseOrderItemPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseOrderItemPersistence.save(entity);
  }
}