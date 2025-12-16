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

	@Autowired
	protected ServerApplicationModel model;
	
	protected Map<String, String> headers = new HashMap<>();
	
	public BearerTokenHeaderProvider(ServerApplicationModel model) {
		this.model = model;
	}

	@Override
	public Map<String, String> get() {
		if (model.getTokenResponse() != null) {
			headers.put("Authorization", "Bearer " + model.getTokenResponse().access_token());
		} else {
			logger.error("No access token available.");
		}
		
		return headers;
	}
}
