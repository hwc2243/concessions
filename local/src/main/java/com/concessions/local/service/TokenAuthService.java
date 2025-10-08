package com.concessions.local.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.Map;

import com.concessions.local.service.TokenAuthService.TokenResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Token Authentication Service implementing the secure Device Authorization Grant (Device Code Flow)
 * and the Refresh Token Grant.
 */
public class TokenAuthService {

    // --- Configuration ---
    // NOTE: In a real application, these should be loaded from configuration, not hardcoded.
    private static final String KEYCLOAK_URL = "https://login.connors.ddns.net/realms/concession";
    private static final String CLIENT_ID = "local"; // Must be a Public Client in Keycloak
    private static final String DEVICE_AUTH_ENDPOINT = KEYCLOAK_URL + "/protocol/openid-connect/auth/device";
    private static final String TOKEN_ENDPOINT = KEYCLOAK_URL + "/protocol/openid-connect/token";

	private static final Preferences PREFS = Preferences.userNodeForPackage(TokenAuthService.class);
	private static final String PREF_ACCESS_TOKEN = "accessToken";
	private static final String PREF_REFRESH_TOKEN = "refreshToken";
	private static final String PREF_EXPIRY_TIME = "tokenExpiryTime";


    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Gson gson = new Gson();

    // Executor service for handling the polling and refresh processes
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
        long interval = Math.max(deviceCodeResponse.interval(), 5);

        scheduler.scheduleWithFixedDelay(() -> {
            if (tokenFuture.isDone()) return;
            try {
                // Now expects the full token response
                TokenResponse token = attemptTokenExchange(deviceCodeResponse.device_code()); 
                
                // If successful, complete the future and stop polling
                tokenFuture.complete(token);
                scheduler.shutdown();
            } catch (TokenPendingException ignored) {
                // Keycloak responded with "authorization_pending". Continue polling.
            } catch (Exception e) {
                // Handle token expired, slow down, or other fatal errors
                if (!tokenFuture.isDone()) {
                    tokenFuture.completeExceptionally(e);
                }
                scheduler.shutdown();
            }
        }, interval, interval, TimeUnit.SECONDS);

        // Schedule a task to terminate polling if the code expires
        scheduler.schedule(() -> {
            if (!tokenFuture.isDone()) {
                tokenFuture.completeExceptionally(new RuntimeException("Device code expired. Please restart login."));
                scheduler.shutdown();
            }
        }, deviceCodeResponse.expires_in(), TimeUnit.SECONDS);
        
        return tokenFuture;
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
		PREFS.remove(PREF_ACCESS_TOKEN);
		PREFS.remove(PREF_REFRESH_TOKEN);
		PREFS.remove(PREF_EXPIRY_TIME);
		try {
			PREFS.flush();
		} catch (java.util.prefs.BackingStoreException e) {
			System.err.println("Failed to clear preferences: " + e.getMessage());
		}
	}
	
	/**
	 * Loads the stored token response from Java Preferences.
	 * 
	 * @return The TokenResponse object, or null if no tokens are found.
	 */
	public TokenResponse retrieveTokenResponse() {
		String accessToken = PREFS.get(PREF_ACCESS_TOKEN, null);
		String refreshToken = PREFS.get(PREF_REFRESH_TOKEN, null);
		long expiryEpochSeconds = PREFS.getLong(PREF_EXPIRY_TIME, 0);

		if (accessToken != null && refreshToken != null) {
			// Calculate remaining seconds for the TokenResponse constructor
			long currentTimeSeconds = System.currentTimeMillis() / 1000;
			long remainingSeconds = Math.max(0, expiryEpochSeconds - currentTimeSeconds);

			// Recreate the TokenResponse object.
			// Note: We use the *remaining* seconds for 'expires_in' if checking expiration
			// outside this class,
			// but since we only use this method to check if a token exists, the exact
			// expires_in isn't critical here.
			// We set it to a dummy value (0) for simplicity in this case,
			// or better yet, we can pass the remainingSeconds if the consumer uses it.
			return new TokenResponse(accessToken, refreshToken, remainingSeconds);
		}
		return null;
	}
	
    /**
	 * Stores the full token response (Access Token, Refresh Token, and Expiry Time)
	 * using the Java Preferences API.
	 */
	public void storeTokenResponse(TokenResponse tokenResponse) {
		// Calculate the absolute expiry time (current time in seconds + token lifetime)
		long expiryEpochSeconds = System.currentTimeMillis() / 1000 + tokenResponse.expires_in();

		PREFS.put(PREF_ACCESS_TOKEN, tokenResponse.access_token());
		PREFS.put(PREF_REFRESH_TOKEN, tokenResponse.refresh_token());
		PREFS.putLong(PREF_EXPIRY_TIME, expiryEpochSeconds);
		// Force the changes to be written to the underlying store
		try {
			PREFS.flush();
		} catch (java.util.prefs.BackingStoreException e) {
			System.err.println("Failed to save preferences: " + e.getMessage());
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
