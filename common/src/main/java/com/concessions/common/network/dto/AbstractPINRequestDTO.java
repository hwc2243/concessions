package com.concessions.common.network.dto;

public abstract class AbstractPINRequestDTO {

	protected String pin;
	
	public AbstractPINRequestDTO () {
	}
	
	public AbstractPINRequestDTO (String pin) {
		this.pin = pin;
	}

	public String getPIN () {
		return pin;
	}

	public void setPIN (String pin) {
		this.pin = pin;
	}
}
