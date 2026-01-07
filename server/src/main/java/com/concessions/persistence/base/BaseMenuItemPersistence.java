package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseMenuItem;
import com.concessions.model.MenuItem;
import com.concessions.model.CategoryType;

public interface BaseMenuItemPersistence<T extends MenuItem, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	
} 