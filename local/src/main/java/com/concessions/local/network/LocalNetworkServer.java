package com.concessions.local.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.concessions.common.network.dto.WelcomeResponseDTO;
import com.concessions.local.model.Device;
import com.concessions.local.network.client.HealthCheckManager;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.network.manager.ManagerRegistry;
import com.concessions.local.service.DeviceService;
import com.concessions.local.util.UdpPortFinder;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Manages the local service discovery and registration process. 1. Binds a TCP
 * ServerSocket to an available port. 2. Finds an available UDP port using
 * UdpPortFinder and listens for "HELLO". 3. Responds with the TCP Server's IP
 * and port in JSON format.
 */
@Component
@ConditionalOnProperty(prefix = "local.network", name = "server", havingValue = "true", matchIfMissing = false // This
																												// ensures
																												// if
																												// the
																												// property
																												// is
																												// not
																												// defined,
																												// the
																												// component
																												// is
																												// NOT
																												// created.
)
public class LocalNetworkServer {

	private static final Logger logger = LoggerFactory.getLogger(LocalNetworkServer.class);

	private static final String HELLO_MESSAGE = "HELLO";
	private static final int BUFFER_SIZE = 256;

	private String serverIp = "";
	private int tcpServerPort = 0;
	private ServerSocket tcpServerSocket;
	private DatagramSocket udpDiscoverySocket;
	private final UdpPortFinder udpPortFinder = new UdpPortFinder();
	private ScheduledExecutorService healthCheckScheduler;

	@Value("${local.network.healhCheckInterval:60}")
	protected int healthCheckInterval;

	@Autowired
	protected DeviceService deviceService;

	@Autowired
	protected ManagerRegistry registry;

	@Autowired
	protected ObjectMapper mapper;

	@Autowired
	protected Messenger messenger;

	@PostConstruct
	public void start() throws IOException {
		// find the best local ip address for our server
		serverIp = findBestLocalIpAddress();

		// Find and bind the TCP Server Socket
		findAndBindAvailableTcpPort();
		logger.info("LocalNetworkServer bound successfully on {}:{}", serverIp, tcpServerPort);

		// Find and bind the UDP Discovery Socket
		udpDiscoverySocket = udpPortFinder.findAndBindAvailablePort();

		// Start the UDP Listener thread
		Thread udpListenerThread = new Thread(this::startUdpListener, "LocalNetworkServer-Discovery-Listener");
		udpListenerThread.start();

		// Start the TCP Listener thread to accept client connections
		Thread tcpListenerThread = new Thread(this::startTcpListener, "LocalNetworkServer-TCP-Listener");
		tcpListenerThread.start();

		// Initialize the health check scheduler
		healthCheckScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setName("LocalNetworkServer-Health-Checker");
			t.setDaemon(true); // Allow the application to exit if this is the only remaining thread
			return t;
		});

		healthCheckScheduler.scheduleAtFixedRate(this::deviceHealthChecker, 60, // initial delay in seconds
				healthCheckInterval, // period in seconds
				TimeUnit.SECONDS);

		logger.info("LocalNetworkServer is active. IP Address: {}, TCP Port: {}, UDP Port: {}", serverIp, tcpServerPort,
				udpDiscoverySocket.getLocalPort());
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

				// Start the TCP Listener thread to accept client connections
				Thread requestHandlerThread = new Thread(() -> startRequestHandler(clientSocket),
						"LocalNetworkServer-Request-Handler-" + clientSocket.getRemoteSocketAddress());
				requestHandlerThread.start();

			}
		} catch (SocketException e) {
			if (!tcpServerSocket.isClosed()) {
				logger.error("TCP Listener socket error: {}", e.getMessage(), e);
			}
		} catch (IOException e) {
			logger.error("TCP Listener I/O error: {}", e.getMessage(), e);
		}
	}

	private void startRequestHandler(Socket socket) {
		String service = null;
		String action = null;
		String payload = null;

		try (
				// Use BufferedReader to easily read lines of text from the socket's input
				// stream
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			logger.debug("[{}] Starting request handler for: {}", Thread.currentThread().getName(),
					socket.getRemoteSocketAddress());

			// 1. Read the full message string
			String requestLine = reader.readLine();

			if (requestLine == null) {
				logger.warn("[{}] Received empty or null request from: {}", Thread.currentThread().getName(),
						socket.getRemoteSocketAddress());
				return;
			}

			logger.debug("[{}] Raw request: {}", Thread.currentThread().getName(), requestLine);

			// 2. Parse the string into service, action, and payload using "|" as the
			// delimiter
			String[] parts = requestLine.split("\\|", 3); // Limit split to 3 parts

			if (parts.length < 3) {
				logger.error("[{}] Malformed request (expected 3 parts, got {}): {}", Thread.currentThread().getName(),
						parts.length, requestLine);
				// If malformed, we can skip processing and let the finally block close the
				// socket.
				return;
			}

			// 3. Assign the parts to the variables (trimming whitespace is good practice)
			service = parts[0].trim();
			action = parts[1].trim();
			payload = parts[2].trim();

			logger.info("[{}] Parsed Request: Service={}, Action={}, Payload={}", Thread.currentThread().getName(),
					service, action, payload);

			try {
				Object response = registry.handleRequest(service, action, payload);
				sendResponse(socket, "OK|" + mapper.writeValueAsString(response));
			} catch (Exception ex) {
				SimpleResponseDTO response = new SimpleResponseDTO();
				response.setMessage(ex.getMessage());
				sendResponse(socket, "ERROR|" + mapper.writeValueAsString(response));
			}

		} catch (IOException e) {
			logger.error("[{}] I/O error processing request from {}: {}", Thread.currentThread().getName(),
					socket.getRemoteSocketAddress(), e.getMessage(), e);
		} catch (Exception e) {
			logger.error("[{}] Unexpected error for {}: {}", Thread.currentThread().getName(),
					socket.getRemoteSocketAddress(), e.getMessage(), e);
		} finally {
			// CRITICAL: Close the socket when processing is finished in the worker thread
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
					logger.debug("[{}] Closed connection from: {}", Thread.currentThread().getName(),
							socket.getRemoteSocketAddress());
				}
			} catch (IOException e) {
				logger.error("[{}] Error closing socket: {}", Thread.currentThread().getName(), e.getMessage(), e);
			}
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
						packet.getAddress().getHostAddress(), packet.getPort(), receivedMessage);

				if (HELLO_MESSAGE.equals(receivedMessage)) {
					// Construct the response payload
					String serverIp = findBestLocalIpAddress();
					WelcomeResponseDTO welcomeResponse = new WelcomeResponseDTO();
					welcomeResponse.setServerIp(serverIp);
					welcomeResponse.setServerPort(tcpServerPort);

					String responseMessage = "WELCOME:" + mapper.writeValueAsString(welcomeResponse);

					byte[] responseData = responseMessage.getBytes();

					// Create a response packet addressed back to the sender
					DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
							packet.getAddress(), packet.getPort());

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
			shutdown();
		}
	}

	private void deviceHealthChecker() {
		logger.info("Executing scheduled device health check...");
		try {
			List<Device> localDevices = deviceService.findAll();

			if (localDevices == null || localDevices.isEmpty()) {
				logger.debug("No local devices found to check.");
				return;
			}

			for (Device device : localDevices) {
				String ip = device.getDeviceIp();
				Integer port = device.getDevicePort();
				String deviceId = device.getDeviceId();

				if (ip != null && !ip.trim().isEmpty() && port != null && port > 0) {
					logger.debug("Checking health for Device ID: {} at {}:{}", deviceId, ip, port);

					if (!performHealthCheck(device)) {
						device.setDeviceIp(null);
						device.setDevicePort(0);
						deviceService.update(device);
					}
				} else {
					logger.debug("Skipping health check for Device ID: {} - Missing IP/Port.", deviceId);
				}
			}
		} catch (Exception ex) {
			logger.error("Error during device health checker execution: {}", ex.getMessage(), ex);
		}
	}

	private boolean performHealthCheck(Device device) {
		try {
			messenger.sendRequest(device.getDeviceIp(), device.getDevicePort(), HealthCheckManager.NAME, HealthCheckManager.CHECK, "", SimpleResponseDTO.class);
			return true;
		} catch (Exception ex) {
			logger.error("Health check failure for Device ID: {} at {}:{} - {}", device.getDeviceId(),
					device.getDeviceIp(), device.getDevicePort(), ex.getMessage(), ex);
		}
		return false;
	}

	private void sendResponse(Socket socket, String responseString) {
		try {
			// Using PrintWriter with auto-flushing enabled (true) for convenience and
			// immediate delivery
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

			// 1. Send the response string followed by a newline character (\n)
			// The newline is crucial for the client's BufferedReader.readLine() to
			// recognize the end of the message.
			writer.println(responseString);

			logger.debug("[{}] Sent response: {}", Thread.currentThread().getName(), responseString);
		} catch (IOException e) {
			logger.error("[{}] Failed to send response to {}: {}", Thread.currentThread().getName(),
					socket.getRemoteSocketAddress(), e.getMessage());
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

					// Look for an IPv4 address that is site-local (private network range) and not
					// loopback
					if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress()
							&& addr.getHostAddress().indexOf(':') < 0) {
						logger.debug("Found best IP address: {} on interface {}", addr.getHostAddress(),
								net.getDisplayName());
						return addr.getHostAddress();
					}
				}
			}

			// Fallback to the standard (potentially loopback) address if no suitable
			// interface is found
			logger.warn("Could not find a site-local, non-loopback IP address. Falling back to default.");
			return InetAddress.getLocalHost().getHostAddress();
		} catch (SocketException | UnknownHostException e) {
			logger.error("Error determining local IP address, falling back to 127.0.0.1.", e);
			return "127.0.0.1";
		}
	}

	@PreDestroy
	private void shutdown() {
		if (healthCheckScheduler != null)
			healthCheckScheduler.shutdownNow();
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
		LocalNetworkServer manager = new LocalNetworkServer();
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
			manager.shutdown();
		}
	}
}