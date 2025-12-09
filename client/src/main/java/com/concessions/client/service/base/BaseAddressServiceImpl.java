package com.concessions.client.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.model.base.BaseAddress;
import com.concessions.client.model.Address;

import com.concessions.client.service.ServiceException;

import com.concessions.client.repository.AddressPersistence;
import com.concessions.client.repository.base.BaseAddressPersistence;

public abstract class BaseAddressServiceImpl<T extends Address, ID>
  implements BaseAddressService<T, ID> {

  @Autowired
  private BaseAddressPersistence<T,ID> baseAddressPersistence;
  
  @Autowired
  protected AddressPersistence addressPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseAddressPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseAddressPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseAddressPersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseAddressPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseAddressPersistence.save(entity);
  }
}