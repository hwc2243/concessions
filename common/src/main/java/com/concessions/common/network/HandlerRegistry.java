package com.concessions.common.network;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HandlerRegistry {
	private static final Logger logger = LoggerFactory.getLogger(HandlerRegistry.class);

	protected static Map<String, AbstractHandler> registry = new HashMap<>();
	
	public HandlerRegistry() {
	}

	public static void registerManager (AbstractHandler manager)
	{
		logger.info("Registering local network manager: {}", manager.getName());
		registry.put(manager.getName(), manager);
	}
	
	public Object handleRequest (String service, String action, String payload) throws NetworkException
	{
		AbstractHandler manager = registry.get(service);
		if (manager != null) {
			return manager.process(action, payload);
		}
		
		throw new NetworkException("No manager for " + service);
	}
}
