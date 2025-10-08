package com.concessions.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Location;
import com.concessions.model.Organization;
import com.concessions.service.LocationService;
import com.concessions.service.OrganizationService;
import com.concessions.service.ServiceException;
import com.concessions.service.UserService;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/location")
public class LocationController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected UserService userService;
	
	public LocationController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value = {"/", ""})
	public String listLocations (ModelMap model) throws ServiceException {
		List<Location> locations = locationService.findAll();
		Collections.sort(locations);
		
		model.addAttribute("locations", locations);
		return "location/list.html";
	}
	
	@GetMapping(value = {"/create"})
	public String createLocation (ModelMap model) {
		model.addAttribute("newLocation", new Location());
		return "location/create.html";
	}
	
	@PostMapping(value = {"/create"})
	public String createLocation(@ModelAttribute("newLocation") Location newLocation) throws ServiceException {
		newLocation.setOrganizationId(SessionContext.getCurrentOrganization().getId());
		newLocation = locationService.create(newLocation);
		logger.info("Created new location: " + newLocation.getName());
		
		SessionContext.getCurrentUser().getLocations().add(newLocation);
		userService.update(SessionContext.getCurrentUser());
		
		return "redirect:/location";
	}
	
	@GetMapping(value = {"/{locationId}/delete"})
	public String deleteLocation(@PathVariable("locationId") Long locationId) throws ServiceException {
		Location location = locationService.get(locationId);
		if (location != null) {
			logger.info("Deleting location: " + location.getName());
			/* HWC removed because this was updated to unidirectional relationship
			location.getUsers().forEach(user -> {
				user.getLocations().remove(location);
				try {
					userService.update(user);
				} catch (ServiceException e) {
					logger.error("Error updating user " + user.getUsername() + " while deleting location.", e);
				}
			});
			*/
			locationService.delete(locationId);
		} else {
			logger.warn("Location with ID " + locationId + " not found.");
		}
		return "redirect:/location";
	}
	
	@GetMapping(value = {"/{locationId}/edit"})
	public String editLocation(@PathVariable("locationId") Long locationId, ModelMap model) throws ServiceException {
		Location location = locationService.get(locationId);
		if (location != null) {
			model.addAttribute("location", location);
			return "location/edit.html";
		} else {
			logger.warn("Location with ID " + locationId + " not found.");
			return "redirect:/location";
		}
	}
	
	@PostMapping(value = {"/{locationId}/edit"})
	public String editLocation(@PathVariable("locationId") Long locationId, @ModelAttribute("location") Location location) throws ServiceException {
		logger.info("Editing location: " + location.getId());
		Location existingLocation = locationService.get(locationId);
		if (existingLocation != null) {
			locationService.update(location);
			logger.info("Updated location: " + location.getName());
		} else {
			logger.warn("Location with ID " + locationId + " not found.");
		}
		return "redirect:/location";
	}
	
	@GetMapping(value = {"/{locationId}/view"})
	public String viewLocation(@PathVariable("locationId") Long locationId, ModelMap model) throws ServiceException {
		Location location = locationService.get(locationId);
		if (location != null) {
			model.addAttribute("location", location);
			return "location/view.html";
		} else {
			logger.warn("Location with ID " + locationId + " not found.");
			return "redirect:/location";
		}
	}
	
	public String getModule ()
	{
		return "location";
	}
	
	/*
	@GetMapping(value = {"/{organizationId}/select"})
	public String selectOrganization(@PathVariable("organizationId") Long organizationId, @RequestParam(value = "redirect", required = false) String redirectUrl)
			throws ServiceException {
		
		Organization organization = locationService.get(organizationId);
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
	*/
}
