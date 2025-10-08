package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseMenuItemOption;
import com.concessions.model.MenuItemOption;

public interface BaseMenuItemOptionPersistence<T extends MenuItemOption, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	
} 