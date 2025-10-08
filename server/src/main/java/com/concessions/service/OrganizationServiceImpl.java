package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseOrganizationServiceImpl;

import com.concessions.model.Organization;

@Service
public class OrganizationServiceImpl
  extends BaseOrganizationServiceImpl<Organization,Long>
  implements OrganizationService
{
}