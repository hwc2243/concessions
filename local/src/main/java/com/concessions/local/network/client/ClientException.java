package com.concessions.local.network.client;

public class ClientException extends Exception {

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
