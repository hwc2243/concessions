package com.concessions.local.bean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.rest.base.HeaderProvider;
import com.concessions.local.server.model.ServerApplicationModel;

public class BearerTokenHeaderProvider implements HeaderProvider {
	private static final Logger logger = LoggerFactory.getLogger(BearerTokenHeaderProvider.class);

	protected ServerConfiguration serverConfig;
	
	protected Map<String, String> headers = new HashMap<>();
	
	public BearerTokenHeaderProvider(@Autowired ServerConfiguration serverConfig) {
		this.serverConfig = serverConfig;
	}

	@Override
	public Map<String, String> get() {
		if (serverConfig.getTokenResponse() != null) {
			headers.put("Authorization", "Bearer " + serverConfig.getTokenResponse().access_token());
		} else {
			logger.error("No access token available.");
		}
		
		return headers;
	}
}
