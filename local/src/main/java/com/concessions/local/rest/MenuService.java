package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import com.concessions.model.Menu;
import com.google.gson.reflect.TypeToken;

public class MenuService extends AbstractRestService {
	

	public MenuService() {
		// TODO Auto-generated constructor stub
	}

	public List<Menu> findAll () throws IOException, InterruptedException {
		return doGet("/api/external/menu", new TypeToken<List<Menu>>() {}.getType());
	}
}
