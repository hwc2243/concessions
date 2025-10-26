package com.concessions.local.security;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.util.NetworkUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class TokenRefreshManager {

	private static final Logger logger = LoggerFactory.getLogger(TokenRefreshManager.class);

    private ScheduledExecutorService scheduler;
    
    // Holds the reference to the scheduled task, useful for cancellation
    private ScheduledFuture<?> refreshTask;
    
    @Autowired
    private ApplicationModel applicationModel;

    @Autowired
    private TokenAuthService authService; 

    // Define the scheduling parameters
    private static final long REFRESH_INTERVAL_MINUTES = 1; // The token refresh frequency
    private static final long INITIAL_DELAY_MINUTES = 0; // Delay before the first refresh run

    /**
     * Initializes and starts the token refresh scheduler manually.
     * Use 'synchronized' to ensure only one thread can start/stop the scheduler at a time.
     * This method should be called after a successful login or connectivity check.
     */
    @PostConstruct
    public synchronized void startRefreshScheduler() {
    	// Check if the scheduler is already running to prevent duplicates
    	if (scheduler != null && !scheduler.isShutdown()) {
    		logger.warn("Token refresh scheduler is already running. Ignoring start request.");
    		return;
    	}
    	
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	
    	logger.info("Starting token refresh scheduler. Interval: {} minutes.", REFRESH_INTERVAL_MINUTES);
    	
    	// 2. Schedule the recurring task
        refreshTask = scheduler.scheduleAtFixedRate(() -> {
            try {
            	if (NetworkUtil.isConnected()) {
            		applicationModel.setConnected(true);
            		TokenResponse tokenResponse = applicationModel.getTokenResponse();
            		if (tokenResponse != null) {
            			authService.refreshToken(tokenResponse.refresh_token()).thenAccept(newToken -> {
            				logger.info("Access token successfully refreshed.");
    						applicationModel.setTokenResponse(newToken);
    						authService.storeTokenResponse(newToken);
    					}).exceptionally(ex -> {
            				logger.warn("Failed to refresh access token. Must handle re-login flow.");
            				applicationModel.setTokenResponse(null);
            				authService.clearTokenResponse();
    						return null;
    					});
            		}
            		
            	} else {
            		applicationModel.setConnected(false);
    				applicationModel.setTokenResponse(null);
    				authService.clearTokenResponse();
            	}
            } catch (Exception e) {
                logger.error("Unexpected error during token refresh execution:", e);

            }
        }, INITIAL_DELAY_MINUTES, REFRESH_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }
    
    /**
     * Manually shuts down the scheduler gracefully.
     * This method should be called when the application disconnects or exits.
     */
    @PreDestroy
    public synchronized void stopRefreshScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            logger.info("Token refresh scheduler is already stopped or not initialized.");
            return;
        }

        logger.info("Manually shutting down token refresh scheduler...");
        try {
        	// Attempt to cancel the scheduled task
        	if (refreshTask != null && !refreshTask.isDone()) {
        		refreshTask.cancel(true);
        	}
        	
            // Stop receiving new tasks
            scheduler.shutdown();
            
            // Wait for up to 5 seconds for any running task to complete
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Token refresh tasks did not terminate gracefully. Forcing immediate shutdown.");
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
            refreshTask = null;
        }
    }
    
    /**
     * Utility method to check the current state of the scheduler.
     * @return true if the scheduler is initialized and not shut down.
     */
    public boolean isSchedulerRunning() {
    	return scheduler != null && !scheduler.isShutdown() && !scheduler.isTerminated();
    }
}
