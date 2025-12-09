package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseMenuItem;
import com.concessions.client.model.MenuItem;

public interface BaseMenuItemPersistence<T extends MenuItem, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	
} 