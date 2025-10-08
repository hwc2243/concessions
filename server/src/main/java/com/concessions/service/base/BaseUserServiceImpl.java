package com.concessions.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.model.base.BaseUser;
import com.concessions.model.User;

import com.concessions.service.ServiceException;

import com.concessions.persistence.UserPersistence;
import com.concessions.persistence.base.BaseUserPersistence;

public abstract class BaseUserServiceImpl<T extends User, ID>
  implements BaseUserService<T, ID> {

  @Autowired
  private BaseUserPersistence<T,ID> baseUserPersistence;
  
  @Autowired
  protected UserPersistence userPersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseUserPersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseUserPersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseUserPersistence.findAll();
  }

  @Override
  public T fetchByUsername (String username)
  {
	return baseUserPersistence.findFirstByUsername(username);
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseUserPersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseUserPersistence.save(entity);
  }
}