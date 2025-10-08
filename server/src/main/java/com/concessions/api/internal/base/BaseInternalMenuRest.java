package com.concessions.api.internal.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Menu;

import com.concessions.api.internal.base.BaseInternalMenuRest;

public interface BaseInternalMenuRest
{
	public ResponseEntity<Menu> createMenu (Menu menu);
	
    public ResponseEntity<Menu> deleteMenu(Long id); 

	public ResponseEntity<Menu> getMenu (Long id);
	
	public ResponseEntity<List<Menu>> listMenus ();
	
	public ResponseEntity<List<Menu>> searchMenus (
	);
	
    public ResponseEntity<Menu> updateMenu (Long id, Menu menu);
}