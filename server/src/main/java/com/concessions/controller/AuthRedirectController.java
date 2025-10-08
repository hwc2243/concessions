package com.concessions.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.concessions.model.User;
import com.concessions.service.ServiceException;
import com.concessions.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthRedirectController {

	private static final Logger logger = LoggerFactory.getLogger(AuthRedirectController.class);

	@Autowired
	protected UserService userService;
	
	public AuthRedirectController() {
	}

	@GetMapping("/authentication-success")
	public String login (HttpServletRequest request) 
	  throws ServiceException{
		
		boolean registration = (boolean) request.getAttribute("registrationRequired");
		
		if (!registration) {
            return "redirect:/";
        }
		else
		{
			return "redirect:/registration";
		}
		
		/*
		logger.debug("AuthRedirectController: User authenticated");
		Map<String, Object> attributes = principal.getAttributes();
        
        logger.debug("Authenticated User Details:");
        String username = (String) attributes.get("email");
        logger.debug("Username (email): " + username);
        String firstName = (String) attributes.get("given_name");
        logger.debug("First Name: " + firstName);
        String lastName = (String)	attributes.get("family_name");
        logger.debug("Last Name: " + lastName);
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String registrationId = "";
        if (oauth2Token != null) {
        	registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            logger.debug("Registration ID (provider): " + registrationId);
        }
        
        User user = userService.fetchUser(attributes.get("email").toString());
        if (user == null) {
        	user = new User.Builder()
        			.username(username)
        			.firstName(firstName)
        			.lastName(lastName)
        			.authProvider(registrationId)
        			.build();
        	
        	userService.create(user);
        
        	return "redirect:/registration";
        }
        
        return "redirect:/";
        */
	}
}
