package com.concessions.local.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.local.model.base.BaseDevice;
import com.concessions.local.model.Device;

public interface BaseDevicePersistence<T extends Device, ID> extends JpaRepository<T, ID>
{
} 