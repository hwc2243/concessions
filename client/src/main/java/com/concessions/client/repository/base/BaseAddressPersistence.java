package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseAddress;
import com.concessions.client.model.Address;

public interface BaseAddressPersistence<T extends Address, ID> extends JpaRepository<T, ID>
{
} 