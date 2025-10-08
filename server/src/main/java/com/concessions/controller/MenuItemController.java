package com.concessions.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.MenuItem;
import com.concessions.service.MenuItemService;
import com.concessions.service.ServiceException;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/menuitem")
public class MenuItemController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MenuItemController.class);

	@Autowired
	protected MenuItemService menuItemService;
	
	public MenuItemController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value = {"/create"})
	public String createMenuItem(ModelMap model, @RequestParam(value="redirect", required=false) String redirect) throws ServiceException {
		model.addAttribute("newMenuItem", new MenuItem());
		model.addAttribute("redirect", (redirect == null ? "/menu" : redirect));
		
		return "menuitem/create.html";
	}
	
	@PostMapping(value = {"/create"})
	public String createMenuItem (ModelMap model, @ModelAttribute("newMenuItem") MenuItem newMenuItem, @RequestParam(value="redirect", required=false) String redirect) throws ServiceException {
		newMenuItem.setOrganizationId(SessionContext.getCurrentOrganization().getId());
		logger.info("Creating new MenuItem: " + newMenuItem);
		menuItemService.create(newMenuItem);
		
		return "redirect:/" + (redirect == null ? "menu" : redirect);
	}
	
	@GetMapping(value = {"/", "", "/list"})
	public String listMenuItems (ModelMap model) throws ServiceException {
		model.addAttribute("menuItems", menuItemService.findAll());
		
		return "menuitem/list.html";
	}
	
	@Override
	public String getModule () {
		return "menuitem";
	}
}
