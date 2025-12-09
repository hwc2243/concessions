package com.concessions.client.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessions.client.model.base.BaseUser;
import com.concessions.client.model.User;

public interface BaseUserPersistence<T extends User, ID> extends JpaRepository<T, ID>
{

    public T findFirstByUsername (String username);
} 