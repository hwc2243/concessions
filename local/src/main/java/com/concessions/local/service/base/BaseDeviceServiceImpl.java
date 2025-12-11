package com.concessions.local.service.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.model.base.BaseDevice;
import com.concessions.local.model.Device;

import com.concessions.local.service.ServiceException;

import com.concessions.local.persistence.DevicePersistence;
import com.concessions.local.persistence.base.BaseDevicePersistence;

public abstract class BaseDeviceServiceImpl<T extends Device, ID>
  implements BaseDeviceService<T, ID> {

  @Autowired
  private BaseDevicePersistence<T,ID> baseDevicePersistence;
  
  @Autowired
  protected DevicePersistence devicePersistence;
  
  @Override
  public T create (T entity) throws ServiceException
  {
    return baseDevicePersistence.save(entity);
  }
  
  @Override
  public void delete (ID id) throws ServiceException
  {
    baseDevicePersistence.deleteById(id);
  }
  
  @Override
  public List<T> findAll () throws ServiceException
  {
    return baseDevicePersistence.findAll();
  }

  @Override
  public T get (ID id) throws ServiceException
  {
    Optional<T> optional = baseDevicePersistence.findById(id);

    return optional.isEmpty() ? null : optional.get();
  }
  
  @Override
  public T update (T entity) throws ServiceException
  {
    return baseDevicePersistence.save(entity);
  }
}