package com.concessions.local.server.orchestrator;

public class OrderException extends Exception {

	public OrderException(String message) {
		super(message);
	}

	public OrderException(Throwable cause) {
		super(cause);
	}

	public OrderException(String message, Throwable cause) {
		super(message, cause);
	}
}
