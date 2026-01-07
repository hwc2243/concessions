package com.concessions.client.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.model.base.BaseMenuItem;
import com.concessions.client.model.MenuItem;

import com.concessions.client.service.ServiceException;

import com.concessions.client.repository.MenuItemPersistence;
import com.concessions.client.repository.base.BaseMenuItemPersistence;

import com.concessions.client.model.base.Multitenant;
import com.concessions.client.service.base.MultitenantServiceImpl;
import com.concessions.dto.CategoryType;

public abstract class BaseMenuItemServiceImpl<T extends MenuItem & Multitenant, ID>
  extends MultitenantServiceImpl<T, ID>
  implements BaseMenuItemService<T, ID> {

  @Autowired
  private BaseMenuItemPersistence<T,ID> baseMenuItemPersistence;
  
  @Autowired
  protected MenuItemPersistence menuItemPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for creating menuItem");
    }
    
    return baseMenuItemPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    try {
      get(id);
    } catch (ServiceException ex) {
      throw new ServiceException("Access denied for deleting menuItem with id = " + id);
    }
    
    baseMenuItemPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseMenuItemPersistence.findByOrganizationId(tenantDiscriminator.getOrganizationId());
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseMenuItemPersistence.findById(id);

    if (optional.isPresent()) {
      if (!hasAccess(optional.get())) {
		throw new ServiceException("Access denied for menuItem with id = " + id);
	  }
	}
    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for updating menuItem with id = " + entity.getId());
    }
    return baseMenuItemPersistence.save(entity);
  }
}