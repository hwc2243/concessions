package com.concessions.local.server;

public interface ApplicationState {
	public boolean isComplete ();
	
	public void execute ();
}
