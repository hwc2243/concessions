package com.concessions.local.service;

import org.springframework.stereotype.Service;

import com.concessions.local.service.base.BaseLocationConfigurationServiceImpl;

import com.concessions.local.model.LocationConfiguration;

@Service
public class LocationConfigurationServiceImpl
  extends BaseLocationConfigurationServiceImpl<LocationConfiguration,Long>
  implements LocationConfigurationService
{
}