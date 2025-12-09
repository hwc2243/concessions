package com.concessions.local.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class ConnectivityChecker {
	private static final Logger logger = LoggerFactory.getLogger(ConnectivityChecker.class);

	@Value("${authHostName:login.connors.ddns.net}")
	protected String authHostName;
	
    // Define the scheduling parameters
    private static final long REFRESH_INTERVAL_MINUTES = 3; // The token refresh frequency
    private static final long INITIAL_DELAY_MINUTES = 0; // Delay before the first refresh run

	protected List<ConnectivityListener> listeners = new ArrayList<>();

    private ScheduledExecutorService scheduler;
    
    // Holds the reference to the scheduled task, useful for cancellation
    private ScheduledFuture<?> connectivityTask;
	
	public ConnectivityChecker() {
		// TODO Auto-generated constructor stub
	}

    @PostConstruct
    public synchronized void startConnectivityScheduler() {
    	// Check if the scheduler is already running to prevent duplicates
    	if (scheduler != null && !scheduler.isShutdown()) {
    		logger.warn("Connectivity scheduler is already running. Ignoring start request.");
    		return;
    	}
    	
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	
    	logger.info("Starting Connectivity scheduler. Interval: {} minutes.", REFRESH_INTERVAL_MINUTES);
    	
    	// 2. Schedule the recurring task
        connectivityTask = scheduler.scheduleAtFixedRate(() -> {
            try {
            	notifyInternet((NetworkUtil.isConnected() ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED));
            	notifyAuth((NetworkUtil.isConnected(authHostName, 443, 100)? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED));
            } catch (Exception ex) {
                logger.error("Unexpected error during Connectivity execution:", ex);
            }
        }, INITIAL_DELAY_MINUTES, REFRESH_INTERVAL_MINUTES, TimeUnit.MINUTES);

    }
    
    /**
     * Manually shuts down the scheduler gracefully.
     * This method should be called when the application disconnects or exits.
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
     * @return true if the scheduler is initialized and not shut down.
     */
    public boolean isSchedulerRunning() {
    	return scheduler != null && !scheduler.isShutdown() && !scheduler.isTerminated();
    }
    
	public void addConnectivityListener (ConnectivityListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}
	
	public void removeConnectivityListener (ConnectivityListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}
	
	protected void notifyInternet (ConnectionStatus status) {
		listeners.stream().forEach(listener -> listener.onInternetNotification(status));
	}

	protected void notifyAuth (ConnectionStatus status) {
		listeners.stream().forEach(listener -> listener.onAuthNotification(status));
	}

	protected void notifyServer (ConnectionStatus status) {
		listeners.stream().forEach(listener -> listener.onServerNotification(status));
	}
	
	public interface ConnectivityListener {
		public void onInternetNotification (ConnectionStatus status);
		
		public void onAuthNotification (ConnectionStatus status);
		
		public void onServerNotification (ConnectionStatus status);
	}
	
	public enum ConnectionStatus {
		CONNECTED,
		DISCONNECTED
	}
}
