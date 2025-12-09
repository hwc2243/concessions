package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseOrganization;
import com.concessions.client.model.Organization;

public interface BaseOrganizationPersistence<T extends Organization, ID> extends JpaRepository<T, ID>
{
} 