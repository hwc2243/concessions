package com.concessions.local.network.dto;

public abstract class AbstractPINRequestDTO {

	protected String pin;
	
	public AbstractPINRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getPIN () {
		return pin;
	}

	public void setPIN (String pin) {
		this.pin = pin;
	}
}
