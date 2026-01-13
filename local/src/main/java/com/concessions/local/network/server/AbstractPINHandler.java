package com.concessions.local.network.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.AbstractHandler;
import com.concessions.common.network.NetworkException;
import com.concessions.common.network.ServerException;
import com.concessions.common.network.dto.AbstractPINRequestDTO;
import com.concessions.local.server.model.ServerApplicationModel;

public abstract class AbstractPINHandler extends AbstractHandler {

	@Autowired
	protected ServerApplicationModel model;
	
	public AbstractPINHandler() {
		// TODO Auto-generated constructor stub
	}

	protected void validatePIN (AbstractPINRequestDTO request) throws ServerException {
		if (!request.getPIN().equals(model.getPIN())) {
			throw new ServerException("PIN validation failed");
		}
	}
}
