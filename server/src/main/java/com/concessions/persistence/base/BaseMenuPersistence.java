package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseMenu;
import com.concessions.model.Menu;

public interface BaseMenuPersistence<T extends Menu, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	

    public List<T> findByOrganizationIdAndName (Long organizationId, String name);
} 