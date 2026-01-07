package com.concessions.local.network.server;

import com.concessions.common.network.NetworkException;

public class ServerException extends NetworkException {

	public ServerException() {
		super();
	}
	
	public ServerException (String message)	{
		super(message);
	}
	
	public ServerException (Throwable t) {
		super(t);
	}
	
	public ServerException (String message, Throwable t) {
		super(message, t);
	}

}
