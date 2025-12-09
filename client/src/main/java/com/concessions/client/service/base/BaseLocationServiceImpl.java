package com.concessions.client.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.model.base.BaseLocation;
import com.concessions.client.model.Location;

import com.concessions.client.service.ServiceException;

import com.concessions.client.repository.LocationPersistence;
import com.concessions.client.repository.base.BaseLocationPersistence;

import com.concessions.client.model.base.Multitenant;
import com.concessions.client.service.base.MultitenantServiceImpl;

public abstract class BaseLocationServiceImpl<T extends Location & Multitenant, ID>
  extends MultitenantServiceImpl<T, ID>
  implements BaseLocationService<T, ID> {

  @Autowired
  private BaseLocationPersistence<T,ID> baseLocationPersistence;
  
  @Autowired
  protected LocationPersistence locationPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for creating location");
    }
    
    return baseLocationPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    try {
      get(id);
    } catch (ServiceException ex) {
      throw new ServiceException("Access denied for deleting location with id = " + id);
    }
    
    baseLocationPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseLocationPersistence.findByOrganizationId(tenantDiscriminator.getOrganizationId());
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseLocationPersistence.findById(id);

    if (optional.isPresent()) {
      if (!hasAccess(optional.get())) {
		throw new ServiceException("Access denied for location with id = " + id);
	  }
	}
    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for updating location with id = " + entity.getId());
    }
    return baseLocationPersistence.save(entity);
  }
}