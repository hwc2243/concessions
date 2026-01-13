package com.concessions.local.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTester {

	public SocketTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
        
        String host = "192.168.1.59";
        int port = 37791;

        System.out.println("--- Starting Diagnostic for " + host + ":" + port + " ---");

        try {
            // Step 1: DNS / Resolution
            System.out.print("1. Resolving Host... ");
            InetAddress addr = InetAddress.getByName(host);
            System.out.println("Resolved to: " + addr.getHostAddress());

            // Step 2: Interface Selection
            System.out.print("2. Testing reachability (ICMP/Echo)... ");
            if (addr.isReachable(2000)) {
                System.out.println("Success.");
            } else {
                System.out.println("Failed (Normal for some configs).");
            }

            // Step 3: Socket Creation & Connect
            System.out.print("3. Attempting Socket Connection... ");
            long start = System.currentTimeMillis();
            try (Socket socket = new Socket()) {
                // Set a timeout to prevent infinite hanging
                socket.connect(new InetSocketAddress(addr, port), 5000);
                long end = System.currentTimeMillis();
                System.out.println("CONNECTED in " + (end - start) + "ms");
                
                System.out.println("Local Address: " + socket.getLocalAddress() + ":" + socket.getLocalPort());
                System.out.println("Remote Address: " + socket.getRemoteSocketAddress());
            }

        } catch (UnknownHostException e) {
            System.err.println("\nFAIL: Could not resolve host. Check /etc/hosts.");
        } catch (ConnectException e) {
            System.err.println("\nFAIL: Connection Refused. The port is likely closed on the target.");
        } catch (NoRouteToHostException e) {
            System.err.println("\nFAIL: No Route To Host. This is a routing or firewall rejection.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("\nFAIL: General I/O Error.");
            e.printStackTrace();
        }
        
        System.out.println("--- Starting Dual Stack Diagnostic for " + host + ":" + port + " ---");
        try {
            // Get ALL IP addresses associated with this hostname
            InetAddress[] addresses = InetAddress.getAllByName(host);
            System.out.println("Found " + addresses.length + " addresses for " + host);

            for (InetAddress addr : addresses) {
                System.out.println("\nTesting: " + addr.toString() + " (" + (addr instanceof Inet6Address ? "IPv6" : "IPv4") + ")");
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(addr, port), 2000);
                    System.out.println("SUCCESS: Connected to " + addr.getHostAddress());
                    return; // Stop if we find one that works
                } catch (Exception e) {
                    System.out.println("FAILED: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
