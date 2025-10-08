package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseOrganization;
import com.concessions.model.Organization;

public interface BaseOrganizationPersistence<T extends Organization, ID> extends JpaRepository<T, ID>
{
} 