package com.concessions.api.internal.base;

import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.User;

import com.concessions.service.UserService;
import com.concessions.service.ServiceException;

import com.concessions.api.internal.base.BaseInternalUserRest;

public abstract class BaseInternalUserRestImpl implements BaseInternalUserRest
{
	@Autowired
	protected UserService userService;

	@PostMapping
	@Override
	public ResponseEntity<User> createUser (@RequestBody User user)
	{
	  User newUser = null;
	  
	  try
	  {
	    newUser = userService.create(user);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<User> deleteUser(@PathVariable Long id) 
    {
      try
      {
        userService.delete(id);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.noContent().build();
    }

	@GetMapping("/{id}")
	@Override
	public ResponseEntity<User> getUser (@PathVariable Long id)
	{
		User user = null;
		
		try	{
			user = userService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<User>> listUsers ()
    {
        try
        {
        	List<User> users = userService.findAll();
        	return ResponseEntity.ok(users);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<User>> searchUsers (
	)
	{
	  try
	  {
	    List<User> allUsers = userService.findAll();
	    List<User> filteredUsers = allUsers.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredUsers);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user)
    {
      User updatedUser = null;
      
      if (user.getId() == 0) 
      {
      	user.setId(id);
      }
      else if (id != user.getId())
      {
         return ResponseEntity.badRequest().build();
      }
      try
      {
        User existingUser = userService.get(id);
        if (existingUser == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedUser = userService.update(user);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedUser);
    }
}