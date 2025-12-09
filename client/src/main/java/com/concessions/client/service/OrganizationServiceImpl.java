package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseOrganizationServiceImpl;

import com.concessions.client.model.Organization;

@Service
public class OrganizationServiceImpl
  extends BaseOrganizationServiceImpl<Organization,Long>
  implements OrganizationService
{
}