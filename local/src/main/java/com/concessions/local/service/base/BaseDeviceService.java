package com.concessions.local.service.base;

import java.util.List;

import com.concessions.local.model.base.BaseDevice;
import com.concessions.local.model.DeviceTypeType;

public interface BaseDeviceService<T extends BaseDevice, ID> extends EntityService<T, ID> {

	public T fetchByDeviceId (String deviceId);

	public List<T> findByDeviceType (DeviceTypeType deviceType);
}