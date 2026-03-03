package com.concessions.local.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.service.ServerConfigurationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Token Authentication Service implementing the secure Device Authorization Grant (Device Code Flow)
 * and the Refresh Token Grant.
 */
@Service
public class TokenAuthService {

	@Value("${authHostName:login.connors.ddns.net}")
	protected String authHostName;
	
    // --- Configuration ---
    // NOTE: In a real application, these should be loaded from configuration, not hardcoded.
    private final String KEYCLOAK_URL;
    private final String CLIENT_ID = "local"; // Must be a Public Client in Keycloak
    private final String DEVICE_AUTH_ENDPOINT;
    private final String TOKEN_ENDPOINT;

	private static final String PREF_ACCESS_TOKEN = "accessToken";
	private static final String PREF_REFRESH_TOKEN = "refreshToken";
	private static final String PREF_EXPIRY_TIME = "tokenExpiryTime";


    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Gson gson = new Gson();

    @Autowired
    protected ServerConfigurationService serverConfigService;
    
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    
    private final AtomicReference<CompletableFuture<TokenResponse>> activeTokenFuture = new AtomicReference<>();

    // Record to hold the initial response data from the device authorization endpoint
    public record DeviceCodeResponse(
        String device_code, 
        String user_code, 
        String verification_uri,
        long expires_in,
        long interval
    ) {}

    // NEW: Record to hold the full token response (used for initial login and refresh)
    public record TokenResponse(
        String access_token, 
        String refresh_token, 
        long expires_in
    ) {}
    
    public TokenAuthService (@Value("${authHostName:login.connors.ddns.net}") String authHostName) {
    	KEYCLOAK_URL = "https://" + authHostName + "/realms/concession";
    	DEVICE_AUTH_ENDPOINT = KEYCLOAK_URL + "/protocol/openid-connect/auth/device";
    	TOKEN_ENDPOINT = KEYCLOAK_URL + "/protocol/openid-connect/token";
    }
    
    public boolean isTokenValid (TokenResponse tokenResponse) {
		 return tokenResponse != null && tokenResponse.access_token() != null && tokenResponse.expires_in() > 60;
	}

    // --- Request Device Code ---
    public CompletableFuture<DeviceCodeResponse> requestDeviceCode() {
        String requestBody = "client_id=" + CLIENT_ID + "&scope=openid";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(DEVICE_AUTH_ENDPOINT))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();
            
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), DeviceCodeResponse.class);
                } else {
                    throw new RuntimeException("Failed to request device code (Status: " + response.statusCode() + "): " + response.body());
                }
            });
    }

    // Poll for Token ---
    public CompletableFuture<TokenResponse> pollForToken(DeviceCodeResponse deviceCodeResponse) {
        CompletableFuture<TokenResponse> tokenFuture = new CompletableFuture<>();
        activeTokenFuture.set(tokenFuture);
        
        long interval = Math.max(deviceCodeResponse.interval(), 5);

        // Schedule the fixed-delay task using the injected taskScheduler
        final Runnable pollingTask = new Runnable() {
            @Override
            public void run() {
                if (tokenFuture.isDone()) return;
                try {
                    // This is synchronous (blocking) but runs on a background thread managed by taskScheduler
                    TokenResponse token = attemptTokenExchange(deviceCodeResponse.device_code()); 
                    
                    // If successful, complete the future. The scheduled tasks will naturally stop.
                    tokenFuture.complete(token);
                } catch (TokenPendingException ignored) {
                    // authorization_pending or slow_down. Continue polling.
                } catch (Exception e) {
                    // Handle fatal errors (e.g., expired_token, access_denied)
                    if (!tokenFuture.isDone()) {
                        tokenFuture.completeExceptionally(e);
                    }
                }
            }
        };

        // Schedule the polling task and store the handle to cancel it later
        final ScheduledFuture<?> pollingHandle = taskScheduler.scheduleWithFixedDelay(
            pollingTask, 
            Duration.ofMillis(interval * 1000L)
        );

        // Schedule a termination task to kill the polling if the code expires
        taskScheduler.schedule(() -> {
            if (!tokenFuture.isDone()) {
                // Cancel the recurring polling task
                pollingHandle.cancel(true);
                tokenFuture.completeExceptionally(new RuntimeException("Device code expired. Please restart login."));
            }
        }, Instant.now().plusSeconds(deviceCodeResponse.expires_in()));
            
        // Add a handler to cancel the polling task once the tokenFuture completes normally or exceptionally
        tokenFuture.whenComplete((result, ex) -> {
        	activeTokenFuture.compareAndSet(tokenFuture, null);
            if (!pollingHandle.isDone()) {
                // If the polling task is still running, cancel it
                pollingHandle.cancel(true);
            }
        });

        return tokenFuture;
    }
    
    public void cancelPolling() {
        CompletableFuture<TokenResponse> current = activeTokenFuture.getAndSet(null);
        if (current != null && !current.isDone()) {
            // This triggers the whenComplete block inside pollForToken
            current.cancel(true); 
        }
    }
    
    // Refresh Token Grant ---
    /**
     * Exchanges the current refresh token for a new access token and refresh token pair.
     * The application should call this when the access token is about to expire.
     * @param currentRefreshToken The expired refresh token.
     * @return A CompletableFuture containing the new full TokenResponse.
     */
    public CompletableFuture<TokenResponse> refreshToken (String currentRefreshToken) {
        String requestBody = String.format(
            "grant_type=refresh_token&" +
            "client_id=%s&" +
            "refresh_token=%s",
            CLIENT_ID,
            URLEncoder.encode(currentRefreshToken, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TOKEN_ENDPOINT))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();

        // Use the HTTP client's sendAsync for non-blocking execution
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() == 200) {
                    // Success! Return the new token set
                    return parseTokenResponse(response.body());
                } else {
                    // Refresh token has failed (likely expired or revoked)
                    Map<String, String> errorMap = gson.fromJson(response.body(), new TypeToken<Map<String, String>>() {}.getType());
                    String errorDescription = errorMap.getOrDefault("error_description", "Unknown error during refresh.");
                    throw new RuntimeException("Token refresh failed: " + errorDescription);
                }
            }).exceptionally(ex -> {
                 throw new CompletionException("Network error during token refresh.", ex);
            });
    }
    
	/**
	 * Clears all stored token information from Java Preferences.
	 */
	public void clearTokenResponse() {
		try {
			ServerConfiguration serverConfig = serverConfigService.get();
			serverConfig.setTokenResponse(null);
			serverConfigService.save();
		} catch (java.util.prefs.BackingStoreException e) {
			System.err.println("Failed to clear preferences: " + e.getMessage());
		}
	}

    // --- Token Exchange Logic ---
    /**
     * Attempts the token exchange for either device code or refresh token.
     * @param deviceCode The device code obtained in the first step.
     * @return The complete TokenResponse if successful.
     */
    private TokenResponse attemptTokenExchange(String deviceCode) throws Exception {
        String requestBody = String.format(
            "grant_type=urn:ietf:params:oauth:grant-type:device_code&client_id=%s&device_code=%s",
            CLIENT_ID,
            deviceCode
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TOKEN_ENDPOINT))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();
            
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            // Success! Token received
            return parseTokenResponse(response.body());
        } else if (response.statusCode() == 400) {
            Map<String, String> errorMap = gson.fromJson(response.body(), new TypeToken<Map<String, String>>() {}.getType());
            String error = errorMap.get("error");
            
            if ("authorization_pending".equals(error) || "slow_down".equals(error)) {
                // Signal to continue polling
                throw new TokenPendingException(error);
            }
            
            // All other 400 errors (e.g., expired_token, access_denied) are fatal
            throw new RuntimeException("Token exchange failed: " + errorMap.get("error_description"));
        } else {
            // General HTTP error
            throw new RuntimeException("Token endpoint error (Status: " + response.statusCode() + ")");
        }
    }

    // --- Utility Methods ---

    /**
     * Custom exception to signal that polling should continue.
     */
    private static class TokenPendingException extends Exception {
        public TokenPendingException(String message) {
            super(message);
        }
    }

    /**
     * Parses the JSON response from the token endpoint to extract the full token response.
     */
    private TokenResponse parseTokenResponse(String jsonResponse) {
        try {
            // Use Gson to directly map the JSON to the TokenResponse record
            TokenResponse token = gson.fromJson(jsonResponse, TokenResponse.class);
            if (token == null || token.access_token() == null) {
                Map<String, String> result = gson.fromJson(jsonResponse, new TypeToken<Map<String, String>>() {}.getType());
                String error = result.get("error_description");
                if (error != null) {
                    throw new IllegalStateException("Keycloak Error: " + error);
                }
                throw new IllegalStateException("Token response missing 'access_token'");
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token JSON response.", e);
        }
    }
    

}
