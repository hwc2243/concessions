package com.concessions.local.network.manager;

public class ServerException extends Exception {

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
