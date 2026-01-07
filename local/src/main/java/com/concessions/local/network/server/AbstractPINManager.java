package com.concessions.local.network.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.common.network.AbstractManager;
import com.concessions.common.network.NetworkException;
import com.concessions.common.network.dto.AbstractPINRequestDTO;
import com.concessions.local.server.model.ServerApplicationModel;

public abstract class AbstractPINManager extends AbstractManager {

	@Autowired
	protected ServerApplicationModel model;
	
	public AbstractPINManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object process(String action, String payload) throws NetworkException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void validatePIN (AbstractPINRequestDTO request) throws ServerException {
		if (!request.getPIN().equals(model.getPIN())) {
			throw new ServerException("PIN validation failed");
		}
	}
}
