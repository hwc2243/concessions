package com.concessions.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.model.base.BaseMenu;
import com.concessions.model.Menu;

import com.concessions.service.ServiceException;

import com.concessions.persistence.MenuPersistence;
import com.concessions.persistence.base.BaseMenuPersistence;

import com.concessions.model.base.Multitenant;
import com.concessions.service.base.MultitenantServiceImpl;

public abstract class BaseMenuServiceImpl<T extends Menu & Multitenant, ID>
  extends MultitenantServiceImpl<T, ID>
  implements BaseMenuService<T, ID> {

  @Autowired
  private BaseMenuPersistence<T,ID> baseMenuPersistence;
  
  @Autowired
  protected MenuPersistence menuPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for creating menu");
    }
    
    return baseMenuPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    try {
      get(id);
    } catch (ServiceException ex) {
      throw new ServiceException("Access denied for deleting menu with id = " + id);
    }
    
    baseMenuPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseMenuPersistence.findByOrganizationId(tenantDiscriminator.getOrganizationId());
  }

  @Override
  public List<T> findByName (String name)
  {
	return baseMenuPersistence.findByOrganizationIdAndName(tenantDiscriminator.getOrganizationId(),name);
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseMenuPersistence.findById(id);

    if (optional.isPresent()) {
      if (!hasAccess(optional.get())) {
		throw new ServiceException("Access denied for menu with id = " + id);
	  }
	}
    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    if (!hasAccess(entity)) {
      throw new ServiceException("Access denied for updating menu with id = " + entity.getId());
    }
    return baseMenuPersistence.save(entity);
  }
}