package com.concessions.api.external.base;

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

import com.concessions.model.Menu;

import com.concessions.service.MenuService;
import com.concessions.service.ServiceException;

import com.concessions.api.external.base.BaseExternalMenuRest;

public abstract class BaseExternalMenuRestImpl implements BaseExternalMenuRest
{
	@Autowired
	protected MenuService menuService;

	@PostMapping
	@Override
	public ResponseEntity<Menu> createMenu (@RequestBody Menu menu)
	{
	  Menu newMenu = null;
	  
	  try
	  {
	    newMenu = menuService.create(menu);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }	
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newMenu);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Menu> deleteMenu(@PathVariable Long id) 
    {
      try
      {
        menuService.delete(id);
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
	public ResponseEntity<Menu> getMenu (@PathVariable Long id)
	{
		Menu menu = null;
		
		try	{
			menu = menuService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (menu != null) {
            return ResponseEntity.ok(menu);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Menu>> listMenus ()
    {
        try
        {
        	List<Menu> menus = menuService.findAll();
        	return ResponseEntity.ok(menus);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Menu>> searchMenus (
	)
	{
	  try
	  {
	    List<Menu> allMenus = menuService.findAll();
	    List<Menu> filteredMenus = allMenus.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredMenus);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu)
    {
      Menu updatedMenu = null;

      if (menu.getId() == 0) 
      {
      	menu.setId(id);
      }
      else if (id != menu.getId())
      {
        return ResponseEntity.badRequest().build();
      }
      try
      {
        Menu existingMenu = menuService.get(id);
        if (existingMenu == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedMenu = menuService.update(menu);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedMenu);
    }
}