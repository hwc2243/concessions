package com.concessions.common.network;

public class MessengerException extends Exception {

	public MessengerException() {
		super();
	}
	
	public MessengerException (String message)	{
		super(message);
	}
	
	public MessengerException (Throwable t) {
		super(t);
	}
	
	public MessengerException (String message, Throwable t) {
		super(message, t);
	}

}
