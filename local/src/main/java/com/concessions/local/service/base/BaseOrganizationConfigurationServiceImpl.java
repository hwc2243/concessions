package com.concessions.local.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.model.base.BaseOrganizationConfiguration;
import com.concessions.local.model.OrganizationConfiguration;

import com.concessions.local.service.ServiceException;

import com.concessions.local.persistence.OrganizationConfigurationPersistence;
import com.concessions.local.persistence.base.BaseOrganizationConfigurationPersistence;

public abstract class BaseOrganizationConfigurationServiceImpl<T extends OrganizationConfiguration, ID>
  implements BaseOrganizationConfigurationService<T, ID> {

  @Autowired
  private BaseOrganizationConfigurationPersistence<T,ID> baseOrganizationConfigurationPersistence;
  
  @Autowired
  protected OrganizationConfigurationPersistence organizationConfigurationPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseOrganizationConfigurationPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseOrganizationConfigurationPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseOrganizationConfigurationPersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseOrganizationConfigurationPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseOrganizationConfigurationPersistence.save(entity);
  }
}