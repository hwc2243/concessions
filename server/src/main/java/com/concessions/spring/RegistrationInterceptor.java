package com.concessions.spring;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.concessions.model.User;
import com.concessions.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RegistrationInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationInterceptor.class);

	@Autowired
	public UserService userService;
	
	public RegistrationInterceptor() {
		logger.info("RegistrationInterceptor: Initialized");
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("RegistrationInterceptor: Pre-handle method");
        
        // Check if the user is authenticated by Spring Security's OAuth2
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            
            // Get the principal, which is an OAuth2User
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            
            // Extract and log user details from the attributes map
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            logger.debug("Authenticated User Details:");
            String username = (String) attributes.get("email");
            logger.debug("Username (email): " + username);
            String firstName = (String) attributes.get("given_name");
            logger.debug("First Name: " + firstName);
            String lastName = (String)	attributes.get("family_name");
            logger.debug("Last Name: " + lastName);
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            logger.debug("Registration ID (provider): " + registrationId);
            
            User user = userService.fullyFetchByUsername(attributes.get("email").toString());
            if (user == null) {
            	user = new User.Builder()
            			.username(username)
            			.firstName(firstName)
            			.lastName(lastName)
            			.authProvider(registrationId)
            			.build();
            	
            	userService.create(user);
            
            	response.sendRedirect("/registration");
            	
            	return false;
            }
        }
        
        return true; // Return true to continue the request chain, false to stop it
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("RegistrationInterceptor: Post-handle method called for /register");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("RegistrationInterceptor: After-completion method called for /register");
    }
}
