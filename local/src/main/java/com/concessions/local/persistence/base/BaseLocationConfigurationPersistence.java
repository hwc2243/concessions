package com.concessions.local.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.local.model.base.BaseLocationConfiguration;
import com.concessions.local.model.LocationConfiguration;

public interface BaseLocationConfigurationPersistence<T extends LocationConfiguration, ID> extends JpaRepository<T, ID>
{
} 