package com.concessions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Organization;
import com.concessions.service.OrganizationService;
import com.concessions.service.ServiceException;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/organization")
public class OrganizationController extends AbstractController {
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrganizationController.class);
	
	@Autowired
	protected OrganizationService organizationService;
	
	public OrganizationController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value = {"/{organizationId}/select"})
	public String selectOrganization(@PathVariable("organizationId") Long organizationId, @RequestParam(value = "redirect", required = false) String redirectUrl)
			throws ServiceException {
		
		Organization organization = organizationService.get(organizationId);
		if (organization != null) {
			logger.info("Selected organization: " + organization.getName());
			SessionContext.setCurrentOrganization(organization);
		} else {
			logger.warn("Organization with ID " + organizationId + " not found.");
		}
		if (redirectUrl != null && !redirectUrl.isEmpty()) {
			logger.info("Redirecting to: " + redirectUrl);
			return "redirect:" + redirectUrl;
		}
		return "redirect:/organization";
	}
	
	@Override
	public String getModule() {
		return "organization";
	}
}
