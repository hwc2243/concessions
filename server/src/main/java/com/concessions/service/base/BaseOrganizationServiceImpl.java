package com.concessions.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.model.base.BaseOrganization;
import com.concessions.model.Organization;

import com.concessions.service.ServiceException;

import com.concessions.persistence.OrganizationPersistence;
import com.concessions.persistence.base.BaseOrganizationPersistence;


public abstract class BaseOrganizationServiceImpl<T extends Organization, ID>
  implements BaseOrganizationService<T, ID> {

  @Autowired
  private BaseOrganizationPersistence<T,ID> baseOrganizationPersistence;
  
  @Autowired
  protected OrganizationPersistence organizationPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseOrganizationPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseOrganizationPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseOrganizationPersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseOrganizationPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseOrganizationPersistence.save(entity);
  }
}