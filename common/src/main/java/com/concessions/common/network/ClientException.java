package com.concessions.common.network;

public class ClientException extends NetworkException {

	public ClientException() {
		super();
	}
	
	public ClientException (String message)	{
		super(message);
	}
	
	public ClientException (Throwable t) {
		super(t);
	}
	
	public ClientException (String message, Throwable t) {
		super(message, t);
	}

}
