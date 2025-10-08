package com.concessions.spring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.concessions.model.Organization;
import com.concessions.model.User;
import com.concessions.service.OrganizationService;
import com.concessions.service.UserService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter that runs after authentication to extract required context (User ID
 * and Organization ID header) and store it in UserContext.
 */
@Component
public class RestContextFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(RestContextFilter.class);

	private static final String ORGANIZATION_HEADER = "organization_id";

	@Autowired
	protected OrganizationService organizationService;

	@Autowired
	protected UserService userService;

	public RestContextFilter() {
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {

			String userId = jwt.getSubject();
			String userEmail = jwt.getClaimAsString("email");
			logger.debug("RestContextFilter: Authenticated user: " + userEmail);

			try {
				User user = userService.fullyFetchByUsername(userEmail);
				if (user == null) {
					logger.warn("RestContextFilter: No user found for email: " + userEmail);
					filterChain.doFilter(request, response);
					return;
				}
				SessionContext.setCurrentUser(user);

				String organizationIdText = request.getHeader(ORGANIZATION_HEADER);
				if (organizationIdText != null && !organizationIdText.isEmpty()) {
					logger.debug("RestContextFilter: Organization ID from header: " + organizationIdText);
					Long organizationId = Long.parseLong(organizationIdText);
					Organization organization = organizationService.get(organizationId);
					if (organization == null) {
						logger.warn("RestContextFilter: No organization found for ID: " + organizationId);
						filterChain.doFilter(request, response);
						return;
					}
					Set<Organization> userOrgs = new HashSet<>();
					if (user.getOrganization() != null) {
						userOrgs.add(user.getOrganization());
					}
					if (user.getOrganizations() != null) {
						userOrgs.addAll(user.getOrganizations());
					}
					if (!userOrgs.contains(organization)) {
						logger.warn("RestContextFilter: User " + userEmail + " does not belong to organization ID: "
								+ organizationId);
						filterChain.doFilter(request, response);
						return;
					}
					SessionContext.setCurrentOrganization(organization);

				} else {
					logger.warn("RestContextFilter: Missing organization_id header in request.");
				}
				filterChain.doFilter(request, response);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("RestContextFilter: Exception occurred while setting context: " + ex.getMessage());
			} finally {
				SessionContext.clear();
			}

		} else {
			// No authentication or wrong principal type, just continue the chain
			filterChain.doFilter(request, response);
		}
	}
}
