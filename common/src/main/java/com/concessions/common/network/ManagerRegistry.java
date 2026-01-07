package com.concessions.common.network;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ManagerRegistry {
	private static final Logger logger = LoggerFactory.getLogger(ManagerRegistry.class);

	protected static Map<String, AbstractManager> registry = new HashMap<>();
	
	public ManagerRegistry() {
	}

	public static void registerManager (AbstractManager manager)
	{
		logger.info("Registering local network manager: {}", manager.getName());
		registry.put(manager.getName(), manager);
	}
	
	public Object handleRequest (String service, String action, String payload) throws NetworkException
	{
		AbstractManager manager = registry.get(service);
		if (manager != null) {
			return manager.process(action, payload);
		}
		
		throw new NetworkException("No manager for " + service);
	}
}
