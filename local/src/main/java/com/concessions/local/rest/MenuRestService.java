package com.concessions.local.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.concessions.model.Menu;
import com.google.gson.reflect.TypeToken;

@Service
public class MenuRestService extends AbstractRestService {
	

	public MenuRestService() {
		// TODO Auto-generated constructor stub
	}

	public List<Menu> findAll () throws IOException, InterruptedException {
		return doGet("/api/external/menu", new TypeToken<List<Menu>>() {}.getType());
	}
}
