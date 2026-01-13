package com.concessions.local.network;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;

public class NetworkTester {

	public NetworkTester() {
		// TODO Auto-generated constructor stub
	}

    public static void main(String[] args) {
        String host = "192.168.1.59"; // CHANGE ME
        int port = 37791;             // CHANGE ME
        int timeout = 5000;

        System.out.println("Starting Java Network Test...");
        System.out.println("Target: " + host + ":" + port);
        System.out.println("IPv4 Preference: " + System.getProperty("java.net.preferIPv4Stack"));

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            System.out.println("SUCCESS: Connected to " + host);
        } catch (NoRouteToHostException e) {
            System.err.println("FAILED: No Route to Host. Check firewall/IPv6 settings.");
            e.printStackTrace();
        } catch (ConnectException e) {
            System.err.println("FAILED: Connection Refused. The port might be closed.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("FAILED: General Error.");
            e.printStackTrace();
        }
    }
}
