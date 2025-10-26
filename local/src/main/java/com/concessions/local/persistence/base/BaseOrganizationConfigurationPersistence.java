package com.concessions.local.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.local.model.base.BaseOrganizationConfiguration;
import com.concessions.local.model.OrganizationConfiguration;

public interface BaseOrganizationConfigurationPersistence<T extends OrganizationConfiguration, ID> extends JpaRepository<T, ID>
{
} 