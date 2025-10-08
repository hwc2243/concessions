package com.concessions.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.concessions.core.ClientContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ClientInterceptor implements HandlerInterceptor {
	private final static Logger logger = LoggerFactory.getLogger(ClientInterceptor.class);
	
	// Define the header name used to pass the client ID
    private static final String CLIENT_HEADER = "X-ClientID";

    /**
     * Pre-handle method to extract the tenant ID before the controller is executed.
     *
     * @param request The current HttpServletRequest.
     * @param response The current HttpServletResponse.
     * @param handler The handler (Controller method) that will be executed.
     * @return true to continue the execution chain, false to stop.
     * @throws Exception if an error occurs during processing.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract the client ID from the request header
        String clientIdText = request.getHeader(CLIENT_HEADER);
        long clientId;

        if (clientIdText == null || clientIdText.trim().isEmpty()) {
        	logger.info("ClientInterceptor: No {} header found in request.", CLIENT_HEADER);
            return true;
        }
        try {
			// Attempt to parse the client ID from the header
			clientId = Long.parseLong(clientIdText);
		} catch (NumberFormatException e) {
			// Handle cases where the client ID is not a valid integer
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Invalid " + CLIENT_HEADER + " header value: " + clientIdText);
			return false; // Stop the request processing
		}
        

        // Set the client ID in the ClientContext (ThreadLocal)
        ClientContext.setCurrentClient(clientId);
        logger.info("ClientInterceptor: Set current client to: " + clientId);

        return true; // Continue processing the request
    }

    /**
     * Post-handle method (after controller, before view rendering).
     * Not typically used for tenant context, but available.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // No specific action needed here for tenant context
    }

    /**
     * After completion method (after request is fully processed, including view rendering).
     * Crucial for cleaning up the ThreadLocal to prevent memory leaks in pooled threads.
     *
     * @param request The current HttpServletRequest.
     * @param response The current HttpServletResponse.
     * @param handler The handler (Controller method) that was executed.
     * @param ex Any exception thrown during handler execution.
     * @throws Exception if an error occurs during processing.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear the tenant ID from the TenantContext
        ClientContext.clearCurrentClient();
        System.out.println("TenantInterceptor: Cleared current client.");
    }
}
