package com.concessions.local.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpPortFinder {

	private static final int START_PORT = 9371;
	private static final int MAX_PORT = 9390; // Ending at the beginning of the Dynamic/Private range

	/**
	 * Attempts to find and bind to an available UDP port starting from START_PORT.
	 * The method returns a fully bound DatagramSocket on the first available port.
	 * * @return A DatagramSocket bound to an available port.
	 * 
	 * @throws IOException if no available port can be found within the range.
	 */
	public DatagramSocket findAndBindAvailablePort() throws IOException {
		int currentPort = START_PORT;

		while (currentPort <= MAX_PORT) {
			try {
				// Attempt to create and bind the DatagramSocket to the current port
				DatagramSocket socket = new DatagramSocket(currentPort);
				System.out.printf("Successfully bound UDP service to port: %d\n", currentPort);

				// Return the successfully bound socket
				return socket;

			} catch (SocketException e) {
				// Port is already in use or another binding error occurred.
				// Log the failure (optional) and try the next port.
				System.out.printf("Port %d is unavailable. Trying next port...\n", currentPort);
				currentPort++;
			}
		}

		// If the loop finishes without finding an available port
		throw new IOException(
				String.format("Failed to find an available UDP port in the range %d to %d.", START_PORT, MAX_PORT));
	}
}
