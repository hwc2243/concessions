package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseMenu;
import com.concessions.client.model.Menu;

public interface BaseMenuPersistence<T extends Menu, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	

    public List<T> findByOrganizationIdAndName (Long organizationId, String name);
} 