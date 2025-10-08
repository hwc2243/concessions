package com.concessions.spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.concessions.model.Organization;
import com.concessions.model.User;

import jakarta.servlet.http.HttpSession;

public class SessionContext {

    private static final ThreadLocal<Organization> ORGANIZATION_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        USER_HOLDER.set(user);
    }

    public static User getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static void setCurrentOrganization(Organization organization) {
    	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
    	if (session != null) {
			session.setAttribute("organizationId", organization.getId());
		}
		ORGANIZATION_HOLDER.set(organization);
	}
    
    public static Organization getCurrentOrganization() {
    	return ORGANIZATION_HOLDER.get();
    }
    
    public static void clear() {
        USER_HOLDER.remove();
        ORGANIZATION_HOLDER.remove();
    }
}
