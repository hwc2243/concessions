package com.concessions.common.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// --- Jackson Imports ---
import com.fasterxml.jackson.databind.ObjectMapper;
import com.concessions.common.network.dto.WelcomeResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
// --- End Jackson Imports ---

/**
 * Client application for local service discovery using UDP broadcast/scan.
 * Scans a range of ports, sends a "HELLO" message, and attempts to parse 
 * the "WELCOME:{json}" response into a WelcomeResponseDTO.
 */
@Component
public class RegistrationClient {

    private static final Logger log = LoggerFactory.getLogger(RegistrationClient.class);
    // Initialize Jackson ObjectMapper once for thread-safe use
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final int START_PORT = 9371;
    private static final int MAX_PORT = 9390; // End of the Registered Ports range
    private static final int TIMEOUT_MS = 3000; // 3 seconds timeout
    private static final String HELLO_MESSAGE = "HELLO";
    private static final String RESPONSE_PREFIX = "WELCOME:";
    private static final int BUFFER_SIZE = 512;

    /**
     * Finds the service by scanning UDP ports and sending a discovery message.
     * * @return The WelcomeResponseDTO if the service is found, or null otherwise.
     */
    public WelcomeResponseDTO discoverService() {
        InetAddress localHost;
        try {
            // Find the local IP address to send the discovery packet to
            localHost = InetAddress.getLocalHost();
        } catch (IOException e) {
            log.error("Could not determine local host address.", e);
            return null;
        }

        // Use a DatagramSocket bound to an ephemeral port for the client
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            log.info("Starting UDP service discovery scan on {}:{} to {}", 
                localHost.getHostAddress(), START_PORT, MAX_PORT);

            byte[] helloData = HELLO_MESSAGE.getBytes();
            
            for (int port = START_PORT; port <= MAX_PORT; port++) {
                log.debug("Attempting connection on port: {}", port);
                
                // 1. Send the HELLO message
                DatagramPacket sendPacket = new DatagramPacket(
                    helloData, 
                    helloData.length, 
                    localHost, 
                    port
                );
                
                try {
                    socket.send(sendPacket);
                    
                    // 2. Prepare to receive the response
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    
                    // 3. Wait for the response (will block up to TIMEOUT_MS due to setSoTimeout)
                    socket.receive(receivePacket);
                    
                    // 4. Process response if received before timeout
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                    log.info("Received response from {}:{}: {}", 
                        receivePacket.getAddress().getHostAddress(), receivePacket.getPort(), response);

                    WelcomeResponseDTO responseDto = parseWelcomeResponse(response);
                    if (responseDto != null) {
                        log.info("Service successfully discovered on port {}. Response: {}", port, responseDto);
                        return responseDto;
                    }
                    
                } catch (SocketTimeoutException e) {
                    // Expected error when no service is listening or responding within the timeout
                    log.debug("No response received from port {} within {}ms. Moving to next port.", port, TIMEOUT_MS);
                    // Continue loop to try next port
                } catch (IOException e) {
                    log.error("I/O Error during communication on port {}.", port, e);
                }
            }
            
            log.warn("Service discovery failed. No active service found in the range {}-{}", START_PORT, MAX_PORT);
            return null;

        } catch (SocketException e) {
            log.error("Failed to initialize or close UDP socket for discovery.", e);
            return null;
        }
    }

    /**
     * Parses the response string "WELCOME:{ "serverIp": "x.x.x.x", "serverPort": "x" }"
     * and maps it to a WelcomeResponseDTO using Jackson.
     * * @param response The raw response string.
     * @return A populated DTO or null if parsing fails.
     */
    private WelcomeResponseDTO parseWelcomeResponse(String response) {
        if (!response.startsWith(RESPONSE_PREFIX)) {
            log.warn("Response prefix mismatch. Expected '{}', got: '{}'", RESPONSE_PREFIX, response);
            return null;
        }

        try {
            // Extract the JSON string part (e.g., { "serverIp": "x.x.x.x", "serverPort": "x" })
            String jsonPart = response.substring(RESPONSE_PREFIX.length()).trim();
            
            // Use Jackson's ObjectMapper to deserialize the JSON string
            WelcomeResponseDTO dto = objectMapper.readValue(jsonPart, WelcomeResponseDTO.class);
            
            // Basic validation
            if (dto.getServerIp() != null && dto.getServerPort() > 0) {
                return dto;
            } else {
                log.error("Parsed DTO contains invalid data: {}", dto);
                return null;
            }
        
        // Catch JsonProcessingException for syntax errors, type mismatches, etc.
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON part of the response: '{}'.", response, e);
            return null;
        } catch (Exception e) {
            // Catch any unexpected exceptions during substring or validation
            log.error("Unexpected error during response parsing of '{}'.", response, e);
            return null;
        }
    }
    
    public static void main(String[] args) {
        RegistrationClient client = new RegistrationClient();
        log.info("Starting client discovery...");
        WelcomeResponseDTO dto = client.discoverService();
        
        if (dto != null) {
            log.info("Service found and ready to connect to TCP server at: {}:{}", dto.getServerIp(), dto.getServerPort());
        } else {
            log.info("Service not found after scanning all ports.");
        }
    }
}