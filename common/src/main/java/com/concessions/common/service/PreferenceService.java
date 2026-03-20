package com.concessions.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class PreferenceService {

	private Map<Class<?>, Preferences> prefMap = new HashMap<>();
	private Class<?> appClass;
	
	public PreferenceService(Class<?> appClass) {
		this.appClass = appClass;
	}

	public void clear (String name) throws BackingStoreException
	{
		clear(appClass, name);
	}
	
	public void clear (Class<?> clazz, String name) throws BackingStoreException
	{
		Preferences preferences = getPreferences(clazz);
		preferences.remove(name);
		preferences.flush();
	}
	
	public String get (String name) 
	{
		return get(appClass, name);
	}
	
	public String get (Class<?> clazz, String name)
	{
		return getPreferences(clazz).get(name, null);
	}
	
	public long getLong (String name) {
		return getLong (appClass, name);
	}
	
	public long getLong (Class<?> clazz, String name) {
		String value = getPreferences(clazz).get(name, "-1");
		return (StringUtils.isNotBlank(value) ? Long.parseLong(value) : -1); 
	}
	
	public void save (String name, Object value) throws BackingStoreException
	{
		save(appClass, name, value);
	}
	
	public void save (Class<?> clazz, String name, Object value) throws BackingStoreException
	{
		Preferences preferences = getPreferences(clazz);
		preferences.put(name, value.toString());
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
