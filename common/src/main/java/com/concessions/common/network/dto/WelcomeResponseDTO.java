package com.concessions.common.network.dto;

public class WelcomeResponseDTO {

	private String serverIp;
	private int serverPort;
	
	public WelcomeResponseDTO() {
		// TODO Auto-generated constructor stub
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
    public String toString() {
        return "WelcomeResponseDTO [serverIp=" + serverIp + ", serverPort=" + serverPort + "]";
    }
}
