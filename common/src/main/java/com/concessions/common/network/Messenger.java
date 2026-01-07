package com.concessions.common.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.common.network.dto.SimpleResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Messenger {

	private static final Logger logger = LoggerFactory.getLogger(Messenger.class);

	@Autowired
	protected ObjectMapper mapper;
	
	private String serverIp;
	private int serverPort;
	
	public Messenger () {
	}
	
	public Messenger (ObjectMapper mapper, String serverIp, int serverPort) {
		this.mapper = mapper;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}
	
	public void initialize (String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	public <T> T sendRequest (String service, String action, Object payloadObject, Class<T> responseClass) throws MessengerException {
		return sendRequest(this.serverIp, this.serverPort, service, action, payloadObject, responseClass);
	}
	
	public <T> T sendRequest (String serverIp, int serverPort, String service, String action, Object payloadObject, Class<T> responseClass) throws MessengerException {
		String message = null;
		
		try {
			message = service + "|" + action + "|" + mapper.writeValueAsString(payloadObject);
			logger.info("Sending message: {}", message);
		} catch (JsonProcessingException ex) {
			logger.error("Failed to serialize payload object for {}|{}: {}", 
					 service, action, ex.getMessage(), ex);
			throw new MessengerException("Failed to serialize payload", ex);
		}
		
		try (
				// Connect to the server using the configured IP and Port
				Socket socket = new Socket(serverIp, serverPort);
				
				// Setup PrintWriter for sending data (auto-flush=true ensures immediate sending)
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				
				// Setup BufferedReader for reading the response from the server
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			) {
				// 4. Send the message
				// println() adds a newline character, which the server uses to delimit the message
				writer.println(message);
				
				// 5. Read the server's response
				String rawResponse = reader.readLine();
				
				if (rawResponse == null) {
					logger.warn("Received null response (server closed connection without sending data).");
					return null;
				}
				
				ServerResponse response = parseServerResponse(rawResponse);
				String responseJson = response.payloadJson();
				if (response.isOk()) {
					if (responseJson.isEmpty()) {
						// Handle cases where an OK response has no payload (e.g., void action)
						logger.warn("Server returned OK but the JSON payload was empty.");
						return null; 
					}
					
					try {
						T responseObject = mapper.readValue(responseJson, responseClass);
						return responseObject;
					} catch (JsonProcessingException e) {
						logger.error("Failed to deserialize response JSON into {}: {}", 
									 responseClass.getSimpleName(), e.getMessage(), e);
						throw new MessengerException("Failed to parse response", e);
					}
					
				} else {
					if (responseJson.isEmpty()) {
						logger.error("Server returned ERROR but the JSON payload was empty.");
						throw new MessengerException("Server return ERROR but no message");
					}
					try {
						SimpleResponseDTO responseObject = mapper.readValue(responseJson, SimpleResponseDTO.class);
						logger.error("Server returned ERROR for {}|{}: {}", 
								 service, action, responseObject.getMessage());
						throw new MessengerException(responseObject.getMessage());
					} catch (JsonProcessingException e) {
						logger.error("Failed to deserialize ERROR response");
						throw new MessengerException("Server returned ERROR but failed to parse response", e);
					}
					// Failure: Log the error message from the payload
				}
			} catch (IOException ex) {
				// Catch connection errors, timeouts, or I/O issues during communication
				logger.error("Error communicating with server {}:{}: {}", 
							 serverIp, serverPort, ex.getMessage());
				throw new MessengerException("Communications with server have failed", ex);
			}
	}
	
	private ServerResponse parseServerResponse (String rawResponse) {
		// Expecting format: STATUS|PAYLOAD (e.g., "OK|{...}" or "ERROR|Error message")
		if (rawResponse == null || rawResponse.isEmpty()) {
			return new ServerResponse("ERROR", "Empty response received.");
		}
		
		int separatorIndex = rawResponse.indexOf('|');
		
		if (separatorIndex == -1) {
			// If no separator is found, treat the whole thing as the status/error message
			return new ServerResponse("ERROR", rawResponse.trim());
		}
		
		String status = rawResponse.substring(0, separatorIndex).trim();
		// The rest is the payload/message. If separator is the last char, this will be empty.
		String payloadJson = rawResponse.substring(separatorIndex + 1).trim(); 
		
		return new ServerResponse(status, payloadJson);
	}
	
	public record ServerResponse (String status, String payloadJson) {
		// Utility method to check if the status is success
		public boolean isOk() {
			return "OK".equalsIgnoreCase(status);
		}
	}
}
