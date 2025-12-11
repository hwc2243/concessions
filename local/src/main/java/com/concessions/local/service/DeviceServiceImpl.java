package com.concessions.local.service;

import org.springframework.stereotype.Service;

import com.concessions.local.service.base.BaseDeviceServiceImpl;

import com.concessions.local.model.Device;

@Service
public class DeviceServiceImpl
  extends BaseDeviceServiceImpl<Device,Long>
  implements DeviceService
{
}