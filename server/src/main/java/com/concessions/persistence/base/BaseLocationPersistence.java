package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseLocation;
import com.concessions.model.Location;

public interface BaseLocationPersistence<T extends Location, ID> extends JpaRepository<T, ID>
{
	public List<T> findByOrganizationId (Long organizationId);
	
} 