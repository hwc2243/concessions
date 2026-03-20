package com.concessions.local.bean;

public class ClientConfiguration extends AbstractConfiguration {
	private static final String PROPERTY_CLIENT_IP = "clientIp";
	private static final String PROPERTY_CLIENT_PORT = "clientPort";
	private static final String PROPERTY_SERVER_IP = "serverIp";
	private static final String PROPERTY_SERVER_PORT = "serverPort";
	
	private String clientIp = null;
	private int clientPort = -1;
	private boolean clientRegistered = false;
	private String serverIp = null;
	private int serverPort = -1;
	
	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public boolean isClientRegistered() {
		return clientRegistered;
	}

	public void setClientRegistered(boolean clientRegistered) {
		this.clientRegistered = clientRegistered;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	protected void updateConfigured() {
	}
}
