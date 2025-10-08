package com.concessions.spring;

import java.util.Map;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.concessions.model.User;
import com.concessions.service.OrganizationService;
import com.concessions.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ContextInterceptor implements HandlerInterceptor {

	private final Logger logger = LoggerFactory.getLogger(ContextInterceptor.class);
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected UserService userService;
	
	public ContextInterceptor() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Get the authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof DefaultOidcUser) {
			DefaultOidcUser oidcUser = (DefaultOidcUser)authentication.getPrincipal();
			
			String emailAddress = oidcUser.getEmail();
			
			logger.info("ContextInterceptor: Looking for user : " + emailAddress);
			User user = userService.fullyFetchByUsername(emailAddress);
			if (user == null) {
				// Extract and log user details from the attributes map
	            Map<String, Object> attributes = oidcUser.getAttributes();
	            
	            logger.debug("Authenticated User Details:");
	            String username = (String) attributes.get("email");
	            logger.debug("Username (email): " + username);
	            String firstName = (String) attributes.get("given_name");
	            logger.debug("First Name: " + firstName);
	            String lastName = (String)	attributes.get("family_name");
	            logger.debug("Last Name: " + lastName);
	            String registrationId = ((OAuth2AuthenticationToken)authentication).getAuthorizedClientRegistrationId();
	            logger.debug("Registration ID (provider): " + registrationId);
	            
	            if (user == null) {
	            	user = new User.Builder()
	            			.username(username)
	            			.firstName(firstName)
	            			.lastName(lastName)
	            			.authProvider(registrationId)
	            			.build();
	            	
	            	User newUser = userService.create(user);
	            	SessionContext.setCurrentUser(newUser);
	            
	            	logger.info("ContextInterceptor: New user created: " + user.getUsername());
	            	logger.info("ContextInterceptor: Redirecting to registration page");
	            	response.sendRedirect("/registration");
	            	
	            	return false;
	            }
			}
			logger.info("ContextInterceptor: Current user set to: " + user.getUsername());
			Hibernate.initialize(user.getOrganizations());
			SessionContext.setCurrentUser(user);
			
			HttpSession session = request.getSession(false);

	        // Check if the session exists and if the "userId" attribute is present.
	        if (session != null && session.getAttribute("organizationId") != null) {
	        	long organizationId = (long) session.getAttribute("organizationId");
	        	logger.info("ContextInterceptor: Setting current organization from session attribute for user: " + user.getUsername() + " to organization ID: " + organizationId);
	        	SessionContext.setCurrentOrganization(organizationService.get(organizationId));
	        } else {
	        	if (user.getOrganization() != null) {
	        		logger.info("ContextInterceptor: Setting current organization to primary organization for user: " + user.getUsername());
					SessionContext.setCurrentOrganization(user.getOrganization());
				} else if (user.getOrganizations() == null || user.getOrganizations().isEmpty()) {
					logger.info("ContextInterceptor: {} has no organizations associated.", user.getUsername());
				} else if (user.getOrganizations().size() == 1) {
					logger.info("ContextInterceptor: Setting current organization to the only organization associated with user: " + user.getUsername());
					SessionContext.setCurrentOrganization(user.getOrganizations().get(0));
				} else {
					logger.info("ContextInterceptor: {} has multiple organizations associated. Redirecting to organization selection page.", user.getUsername());
					response.sendRedirect("/organization/select");
				}
	        }
        }
        return true;
    }
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		SessionContext.clear();
	}
}
