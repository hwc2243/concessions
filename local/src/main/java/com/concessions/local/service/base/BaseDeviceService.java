package com.concessions.local.service.base;

import java.util.List;

import com.concessions.local.model.base.BaseDevice;

public interface BaseDeviceService<T extends BaseDevice, ID> extends EntityService<T, ID> {

	public T fetchByDeviceId (String deviceId);
}