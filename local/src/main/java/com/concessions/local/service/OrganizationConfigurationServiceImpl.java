package com.concessions.local.service;

import org.springframework.stereotype.Service;

import com.concessions.local.service.base.BaseOrganizationConfigurationServiceImpl;

import com.concessions.local.model.OrganizationConfiguration;

@Service
public class OrganizationConfigurationServiceImpl
  extends BaseOrganizationConfigurationServiceImpl<OrganizationConfiguration,Long>
  implements OrganizationConfigurationService
{
}