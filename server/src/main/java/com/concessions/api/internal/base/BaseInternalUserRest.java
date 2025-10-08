package com.concessions.api.internal.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.User;

import com.concessions.api.internal.base.BaseInternalUserRest;

public interface BaseInternalUserRest
{
	public ResponseEntity<User> createUser (User user);
	
    public ResponseEntity<User> deleteUser(Long id); 

	public ResponseEntity<User> getUser (Long id);
	
	public ResponseEntity<List<User>> listUsers ();
	
	public ResponseEntity<List<User>> searchUsers (
	);
	
    public ResponseEntity<User> updateUser (Long id, User user);
}