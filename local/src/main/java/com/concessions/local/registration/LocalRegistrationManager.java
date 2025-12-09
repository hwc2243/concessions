package com.concessions.local.registration;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.concessions.local.dto.WelcomeResponseDTO;
import com.concessions.local.util.UdpPortFinder;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Manages the local service discovery and registration process.
 * 1. Binds a TCP ServerSocket to an available port.
 * 2. Finds an available UDP port using UdpPortFinder and listens for "HELLO".
 * 3. Responds with the TCP Server's IP and port in JSON format.
 */
@Component
public class LocalRegistrationManager {

    private static final Logger logger = LoggerFactory.getLogger(LocalRegistrationManager.class);

    private static final String HELLO_MESSAGE = "HELLO";
    private static final int BUFFER_SIZE = 256;

    private String serverIp = "";
    private int tcpServerPort = 0;
    private ServerSocket tcpServerSocket;
    private DatagramSocket udpDiscoverySocket;
    private final UdpPortFinder udpPortFinder = new UdpPortFinder();

    private ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void start() throws IOException {
    	// find the best local ip address for our server
    	serverIp = findBestLocalIpAddress();
    	
        // Find and bind the TCP Server Socket
        findAndBindAvailableTcpPort();
        logger.info("RegistrationManager bound successfully on {}:{}", serverIp, tcpServerPort);

        // Find and bind the UDP Discovery Socket
        udpDiscoverySocket = udpPortFinder.findAndBindAvailablePort();
        
        // Start the UDP Listener thread
        Thread udpListenerThread = new Thread(this::startUdpListener, "RegistrationManager-Discovery-Listener");
        udpListenerThread.start();
        
     // Start the TCP Listener thread to accept client connections
     	 	Thread tcpListenerThread = new Thread(this::startTcpListener, "RegistrationManager-TCP-Listener");
     	 	tcpListenerThread.start();
     	 	
        logger.info("RegistrationManager is active. IP Address: {}, TCP Port: {}, UDP Port: {}", serverIp, tcpServerPort, udpDiscoverySocket.getLocalPort());
    }

    /**
     * Binds a ServerSocket to port 0 to let the OS find an available TCP port.
     */
    private void findAndBindAvailableTcpPort() throws IOException {
        // Binding to port 0 tells the OS to assign any available port
        tcpServerSocket = new ServerSocket(0);
        tcpServerPort = tcpServerSocket.getLocalPort();
    }

    private void startTcpListener() {
        try {
            while (!tcpServerSocket.isClosed()) {
                // Blocks until a connection is made
                Socket clientSocket = tcpServerSocket.accept(); 
                logger.info("[TCP] Accepted connection from: {}", clientSocket.getRemoteSocketAddress());
                
                // In a production application, you would typically hand off clientSocket 
                // to a dedicated worker thread or executor service for processing.
                // For this example, we immediately close the connection.
                clientSocket.close();
                logger.debug("[TCP] Closed connection from: {}", clientSocket.getRemoteSocketAddress());
            }
        } catch (SocketException e) {
            if (!tcpServerSocket.isClosed()) {
                logger.error("TCP Listener socket error: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            logger.error("TCP Listener I/O error: {}", e.getMessage(), e);
        }
    }
    
    /**
     * The main loop for the UDP discovery service.
     */
    private void startUdpListener() {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (!udpDiscoverySocket.isClosed()) {
                // Wait for an incoming UDP packet
                udpDiscoverySocket.receive(packet);

                String receivedMessage = new String(packet.getData(), 0, packet.getLength()).trim();
                
                logger.info("\n[UDP] Received message from {}:{} with content: '{}'", 
                    packet.getAddress().getHostAddress(), 
                    packet.getPort(), 
                    receivedMessage
                );

                if (HELLO_MESSAGE.equals(receivedMessage)) {
                    // Construct the response payload
                    String serverIp = findBestLocalIpAddress();
                    WelcomeResponseDTO welcomeResponse = new WelcomeResponseDTO();
                    welcomeResponse.setServerIp(serverIp);
                    welcomeResponse.setServerPort(tcpServerPort);
                    
                    String responseMessage = "WELCOME:" + mapper.writeValueAsString(welcomeResponse);

                    byte[] responseData = responseMessage.getBytes();
                    
                    // Create a response packet addressed back to the sender
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseData, 
                        responseData.length, 
                        packet.getAddress(), 
                        packet.getPort()
                    );

                    // Send the response
                    udpDiscoverySocket.send(responsePacket);
                    logger.info("[UDP] Responded with: '{}'", responseMessage);
                }
            }
        } catch (SocketException e) {
            if (!udpDiscoverySocket.isClosed()) {
                logger.error("UDP Listener socket error: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            logger.error("UDP Listener I/O error: {}", e.getMessage(), e);
        } finally {
            closeSockets();
        }
    }

    /**
     * Attempts to find the local machine's non-loopback IP address.
     */
    private static String findBestLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface net = nets.nextElement();
                // Skip interfaces that are down, loopback, or virtual
                if (net.isLoopback() || !net.isUp() || net.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = net.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    // Look for an IPv4 address that is site-local (private network range) and not loopback
                    if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') < 0) {
                        logger.debug("Found best IP address: {} on interface {}", addr.getHostAddress(), net.getDisplayName());
                        return addr.getHostAddress();
                    }
                }
            }
            
            // Fallback to the standard (potentially loopback) address if no suitable interface is found
            logger.warn("Could not find a site-local, non-loopback IP address. Falling back to default.");
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
             logger.error("Error determining local IP address, falling back to 127.0.0.1.", e);
             return "127.0.0.1";
        }
    }
    
    @PreDestroy
    private void closeSockets() {
        if (udpDiscoverySocket != null && !udpDiscoverySocket.isClosed()) {
            udpDiscoverySocket.close();
        }
        if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
            try {
                tcpServerSocket.close();
            } catch (IOException e) {
                logger.error("Error closing TCP socket: {}", e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        LocalRegistrationManager manager = new LocalRegistrationManager();
        try {
            manager.start();
            
            // Keep the main thread alive so the UDP listener can run
            Thread.sleep(Long.MAX_VALUE);
            
        } catch (IOException e) {
            logger.error("FATAL: Failed to start Local Registration Controller: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("Local Registration Controller interrupted.", e);
            Thread.currentThread().interrupt();
        } finally {
            manager.closeSockets();
        }
    }
}