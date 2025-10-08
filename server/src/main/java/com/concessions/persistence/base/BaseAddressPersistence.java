package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseAddress;
import com.concessions.model.Address;

public interface BaseAddressPersistence<T extends Address, ID> extends JpaRepository<T, ID>
{
} 