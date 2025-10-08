package com.concessions.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.model.base.BaseMenuItemOption;
import com.concessions.model.MenuItemOption;

import com.concessions.service.ServiceException;

import com.concessions.persistence.MenuItemOptionPersistence;
import com.concessions.persistence.base.BaseMenuItemOptionPersistence;

import com.concessions.model.base.Multitenant;
import com.concessions.service.base.MultitenantServiceImpl;

public abstract class BaseMenuItemOptionServiceImpl<T extends MenuItemOption & Multitenant, ID>
  extends MultitenantServiceImpl<T, ID>
  implements BaseMenuItemOptionService<T, ID> {

  @Autowired
  private BaseMenuItemOptionPersistence<T,ID> baseMenuItemOptionPersistence;
  
  @Autowired
  protected MenuItemOptionPersistence menuItemOptionPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for creating menuItemOption");
    }
    
    return baseMenuItemOptionPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    try {
      get(id);
    } catch (ServiceException ex) {
      throw new ServiceException("Access denied for deleting menuItemOption with id = " + id);
    }
    
    baseMenuItemOptionPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseMenuItemOptionPersistence.findByOrganizationId(tenantDiscriminator.getOrganizationId());
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseMenuItemOptionPersistence.findById(id);

    if (optional.isPresent()) {
      if (!hasAccess(optional.get())) {
		throw new ServiceException("Access denied for menuItemOption with id = " + id);
	  }
	}
    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for updating menuItemOption with id = " + entity.getId());
    }
    return baseMenuItemOptionPersistence.save(entity);
  }
}