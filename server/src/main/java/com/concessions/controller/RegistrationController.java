package com.concessions.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.model.Organization;
import com.concessions.model.User;
import com.concessions.service.OrganizationService;
import com.concessions.service.ServiceException;
import com.concessions.service.UserService;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected UserService userService;
	
	public RegistrationController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value ={"/", ""})
	public String registerOrganization (Model model)
	{
		model.addAttribute("organization", new Organization());
		return "registration/organization.html";
	}
	
	@PostMapping(value ={"/", ""})
	public String create (Organization organization, BindingResult result)
			throws ServiceException
	{
		logger.info("Creating new organization: " + organization.getName());
		Organization newOrganization = organizationService.create(organization);
		User currentUser = SessionContext.getCurrentUser();
		currentUser.setOrganization(newOrganization);
		
		List<Organization> userOrganizations = currentUser.getOrganizations();
		if (userOrganizations == null) {
			userOrganizations = new java.util.ArrayList<Organization>();
			currentUser.setOrganizations(userOrganizations);
		}
		logger.info("Associating user " + currentUser.getUsername() + " with organization " + newOrganization.getName());
		userOrganizations.add(newOrganization);
		userService.update(currentUser);
		
		return "redirect:/home";
	}
}
//