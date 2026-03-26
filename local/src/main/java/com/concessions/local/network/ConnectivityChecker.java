package com.concessions.local.network;

import static com.concessions.local.network.NetworkUtil.HostConfiguration;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.concessions.local.bean.ServerConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class ConnectivityChecker {
	private static final Logger logger = LoggerFactory.getLogger(ConnectivityChecker.class);

	// Define the scheduling parameters
	private static final long REFRESH_INTERVAL_MINUTES = 3; // The token refresh frequency
	private static final long INITIAL_DELAY_MINUTES = 0; // Delay before the first refresh run

	@Value("${apiHostName: http://localhost:8080}")
	protected String apiHostname;

	@Value("${authHostName:login.connors.ddns.net}")
	protected String authHostname;
	
	@Autowired
	protected ServerConfiguration serverConfig;

	protected HostConfiguration authConfiguration;
	protected HostConfiguration apiConfiguration;

	private ScheduledExecutorService scheduler;

	// Holds the reference to the scheduled task, useful for cancellation
	private ScheduledFuture<?> connectivityTask;

	public ConnectivityChecker() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public synchronized void startConnectivityScheduler() {
		apiConfiguration = parseHost(apiHostname);
		authConfiguration = parseHost(authHostname);

		// Check if the scheduler is already running to prevent duplicates
		if (scheduler != null && !scheduler.isShutdown()) {
			logger.warn("Connectivity scheduler is already running. Ignoring start request.");
			return;
		}

		scheduler = Executors.newSingleThreadScheduledExecutor();

		logger.info("Starting Connectivity scheduler. Interval: {} minutes.", REFRESH_INTERVAL_MINUTES);

		// 2. Schedule the recurring task
		connectivityTask = scheduler.scheduleAtFixedRate(() -> {
			ConnectionStatus internetConnected = ConnectionStatus.DISCONNECTED;
			ConnectionStatus authConnected = ConnectionStatus.DISCONNECTED;
			ConnectionStatus apiConnected = ConnectionStatus.DISCONNECTED;
			try {
				internetConnected = (NetworkUtil.isConnected() ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED);
				authConnected = (NetworkUtil.isConnected(authConfiguration) ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED);
				apiConnected = (NetworkUtil.isConnected(apiConfiguration) ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED);
			} catch (Exception ex) {
				logger.error("Unexpected error during Connectivity execution:", ex);
			}
			
			serverConfig.setInternetConnected(internetConnected);
			serverConfig.setAuthConnected(authConnected);
			serverConfig.setApiConnected(apiConnected);
			
		}, INITIAL_DELAY_MINUTES, REFRESH_INTERVAL_MINUTES, TimeUnit.MINUTES);

	}

	/**
	 * Manually shuts down the scheduler gracefully. This method should be called
	 * when the application disconnects or exits.
	 */
	@PreDestroy
	public synchronized void stopConnectivityScheduler() {
		if (scheduler == null || scheduler.isShutdown()) {
			logger.info("Connectivity scheduler is already stopped or not initialized.");
			return;
		}

		logger.info("Manually shutting down connectivity scheduler...");
		try {
			// Attempt to cancel the scheduled task
			if (connectivityTask != null && !connectivityTask.isDone()) {
				connectivityTask.cancel(true);
			}

			// Stop receiving new tasks
			scheduler.shutdown();

			// Wait for up to 5 seconds for any running task to complete
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.warn("Connectivity tasks did not terminate gracefully. Forcing immediate shutdown.");
				// Force shut down if tasks don't finish
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			// Re-interrupt the current thread and force immediate shutdown
			Thread.currentThread().interrupt();
			logger.error("Shutdown interrupted.", e);
			scheduler.shutdownNow();
		} finally {
			// Clear the reference regardless of success/failure
			scheduler = null;
			connectivityTask = null;
		}
	}

	/**
	 * Utility method to check the current state of the scheduler.
	 * 
	 * @return true if the scheduler is initialized and not shut down.
	 */
	public boolean isSchedulerRunning() {
		return scheduler != null && !scheduler.isShutdown() && !scheduler.isTerminated();
	}

	public HostConfiguration parseHost(String input) {
		if (input == null || input.isBlank()) {
			throw new IllegalArgumentException("Host configuration string cannot be null or empty");
		}

		String workingString = input.trim();
		boolean hasProtocol = workingString.contains("://");

		// If no protocol, we add a dummy one so URI can parse it
		if (!hasProtocol) {
			workingString = "https://" + workingString;
		}

		try {
			URI uri = new URI(workingString);
			String hostname = uri.getHost().toLowerCase();
			
			InetAddress.getByName(hostname);
			
			int port = uri.getPort();
			String scheme = uri.getScheme();

			// Resolve the Port based on your rules
			int finalPort;
			if (port != -1) {
				// Port is explicitly specified (e.g., foo.com:443)
				finalPort = port;
			} else if (hasProtocol && "http".equalsIgnoreCase(scheme)) {
				// Protocol is http and no port specified
				finalPort = 80;
			} else {
				// No port specified, and either protocol is https or no protocol was provided
				finalPort = 443;
			}
			
			return HostConfiguration.builder().hostname(hostname).port(finalPort).build();
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid host configuration format: " + input, ex);
		}
		catch (UnknownHostException ex) {
			throw new IllegalArgumentException("Invalid hostname: " + input, ex);
		}
	}

	public interface ConnectivityListener {
		public void onInternetNotification(ConnectionStatus status);

		public void onAuthNotification(ConnectionStatus status);

		public void onServerNotification(ConnectionStatus status);
	}




	public enum ConnectionStatus {
		CONNECTED, DISCONNECTED
	}
}
