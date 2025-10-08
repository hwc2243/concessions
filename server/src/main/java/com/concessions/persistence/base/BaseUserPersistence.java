package com.concessions.persistence.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.model.base.BaseUser;
import com.concessions.model.User;

public interface BaseUserPersistence<T extends User, ID> extends JpaRepository<T, ID>
{

    public T findFirstByUsername (String username);
} 