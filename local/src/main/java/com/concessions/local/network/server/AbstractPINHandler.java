package com.concessions.local.network.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.NetworkException;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.AbstractPINRequestDTO;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.network.AbstractHandler;
import com.concessions.local.server.model.ServerApplicationModel;

public abstract class AbstractPINHandler extends AbstractHandler {

	@Autowired
	protected ApplicationConfiguration appConfig;
	
	public AbstractPINHandler() {
		// TODO Auto-generated constructor stub
	}

	protected void validatePIN (AbstractPINRequestDTO request) throws ServerException {
		if (!request.getPIN().equals(appConfig.getPin())) {
			throw new ServerException("PIN validation failed");
		}
	}
}
