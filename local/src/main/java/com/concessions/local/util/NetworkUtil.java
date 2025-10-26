package com.concessions.local.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utility class for checking network connectivity using a simple socket.
 */
public class NetworkUtil {

    private static final String DEFAULT_HOST = "8.8.8.8"; // Google's public DNS server
    private static final int DEFAULT_PORT = 53;          // DNS port
    private static final int DEFAULT_TIMEOUT_MS = 3000;  // 3 seconds timeout

    /**
     * Checks if the host machine has an active internet connection by attempting 
     * to connect to a well-known public server (Google DNS by default).
     *
     * @return true if a connection can be successfully opened, false otherwise.
     */
    public static boolean isConnected() {
        return isConnected(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Checks if a specific host and port are reachable within a timeout period.
     * * @param host The hostname or IP address (e.g., "www.google.com").
     * @param port The port number (e.g., 80 for HTTP, 443 for HTTPS).
     * @param timeoutMs The connection timeout in milliseconds.
     * @return true if the socket connection is successful, false otherwise.
     */
    public static boolean isConnected(String host, int port, int timeoutMs) {
        // Use try-with-resources to ensure the socket is always closed
        try (Socket socket = new Socket()) {
            // Attempt to connect to the host:port within the timeout
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            System.out.println("Connection successful to " + host + ":" + port);
            return true;
        } catch (IOException e) {
            // This catches UnknownHostException, ConnectionException, and SocketTimeoutException
            System.out.println("Connection failed. Host " + host + " is unreachable or connection timed out.");
            // Log the error detail for debugging, but return false for the connectivity check
            // e.printStackTrace(); 
            return false;
        }
    }
}
