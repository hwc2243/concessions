package com.concessions.local.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.model.base.BaseLocationConfiguration;
import com.concessions.local.model.LocationConfiguration;

import com.concessions.local.service.ServiceException;

import com.concessions.local.persistence.LocationConfigurationPersistence;
import com.concessions.local.persistence.base.BaseLocationConfigurationPersistence;

public abstract class BaseLocationConfigurationServiceImpl<T extends LocationConfiguration, ID>
  implements BaseLocationConfigurationService<T, ID> {

  @Autowired
  private BaseLocationConfigurationPersistence<T,ID> baseLocationConfigurationPersistence;
  
  @Autowired
  protected LocationConfigurationPersistence locationConfigurationPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseLocationConfigurationPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseLocationConfigurationPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseLocationConfigurationPersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseLocationConfigurationPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseLocationConfigurationPersistence.save(entity);
  }
}