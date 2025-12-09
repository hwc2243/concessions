package com.concessions.local.service;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Service;

import com.concessions.local.server.ServerApplication;


@Service
public class PreferenceService {

	private Map<Class<?>, Preferences> prefMap = new HashMap<>();

	public PreferenceService() {
		System.out.println("PreferenceService created");
	}

	public void clear (String name) throws BackingStoreException
	{
		clear(ServerApplication.class, name);
	}
	
	public void clear (Class<?> clazz, String name) throws BackingStoreException
	{
		Preferences preferences = getPreferences(clazz);
		preferences.remove(name);
		preferences.flush();
	}
	
	public String get (String name) 
	{
		return get(ServerApplication.class, name);
	}
	
	public String get (Class<?> clazz, String name)
	{
		return getPreferences(clazz).get(name, null);
	}
	
	public void save (String name, String value) throws BackingStoreException
	{
		save(ServerApplication.class, name, value);
	}
	
	public void save (Class<?> clazz, String name, String value) throws BackingStoreException
	{
		Preferences preferences = getPreferences(clazz);
		preferences.put(name, value);
		preferences.flush();
	}
	
	
	protected Preferences getPreferences (Class<?> clazz)
	{
		Preferences preferences = prefMap.get(clazz);
		if (preferences == null) {
			preferences = Preferences.userNodeForPackage(clazz);
			prefMap.put(clazz, preferences);
		} 
		
		return preferences;
	}
}
