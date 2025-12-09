package com.concessions.local.bean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.client.rest.base.HeaderProvider;
import com.concessions.local.ui.model.ApplicationModel;

public class BearerTokenHeaderProvider implements HeaderProvider {
	private static final Logger logger = LoggerFactory.getLogger(BearerTokenHeaderProvider.class);

	@Autowired
	protected ApplicationModel model;
	
	protected Map<String, String> headers = new HashMap<>();
	
	public BearerTokenHeaderProvider(ApplicationModel model) {
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
