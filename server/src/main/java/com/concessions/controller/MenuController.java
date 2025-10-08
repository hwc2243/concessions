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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.concessions.model.Location;
import com.concessions.model.Menu;
import com.concessions.model.MenuItem;
import com.concessions.model.Organization;
import com.concessions.service.LocationService;
import com.concessions.service.MenuItemService;
import com.concessions.service.MenuService;
import com.concessions.service.OrganizationService;
import com.concessions.service.ServiceException;
import com.concessions.service.UserService;
import com.concessions.spring.SessionContext;

@Controller
@RequestMapping("/menu")
public class MenuController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	protected MenuService menuService;
	
	@Autowired
	protected MenuItemService menuItemService;
	
	@Autowired
	protected UserService userService;
	
	public MenuController() {
		// TODO Auto-generated constructor stub
	}

	@GetMapping(value = {"/", ""})
	public String listMenus (ModelMap model) throws ServiceException {
		List<Menu> menus = menuService.findAll();
		Collections.sort(menus);
		model.addAttribute("menus", menus);
		model.addAttribute("menuItems", menuItemService.findAll());
		
		return "menu/list.html";
	}
	
	@GetMapping(value = {"/create"})
	public String createMenu (ModelMap model) {
		model.addAttribute("newMenu", new Menu());
		
		return "menu/create.html";
	}
	
	@PostMapping(value = {"/create"})
	public String createMenu(@ModelAttribute("newMenu") Menu newMenu) throws ServiceException {
		newMenu.setOrganizationId(SessionContext.getCurrentOrganization().getId());
		newMenu = menuService.create(newMenu);
		logger.info("Created new menu: " + newMenu.getName());
		
		return "redirect:/menu";
	}
	
	@GetMapping(value = {"/{menuId}/delete"})
	public String deleteMenu(@PathVariable("menuId") Long menuId) throws ServiceException {
		Menu menu = menuService.get(menuId);
		
		if (menu != null) {
			logger.info("Deleting menu: " + menu.getName());
			menuService.delete(menuId);
		} else {
			logger.warn("menu with ID " + menuId + " not found.");
		}
		return "redirect:/menu";
	}

	@GetMapping(value = {"/{menuId}/edit"})
	public String editMenu(@PathVariable("menuId") Long menuId, ModelMap model) throws ServiceException {
		Menu menu = menuService.get(menuId);
		if (menu == null) {
			logger.warn("Menu with ID " + menuId + " not found.");
			return "redirect:/menu";
		}
		model.addAttribute("menu", menu);
		model.addAttribute("availableMenuItems", menuItemService.findAll());
		
		return "menu/edit.html";
	}
	
	@PostMapping(value = {"/{menuId}/edit"})
	public String editMenu(@PathVariable("menuId") Long menuId, Menu menu) throws ServiceException {
		if (menuId != menu.getId()) {
			logger.warn("Menu ID in path does not match Menu ID in form data.");
			return "redirect:/menu";
		}
		Menu existingMenu = menuService.get(menuId);
		menu.setMenuItems(existingMenu.getMenuItems());
		menu.setOrganizationId(SessionContext.getCurrentOrganization().getId());
		menuService.update(menu);
		
		return "redirect:/menu";
	}
	
	@PostMapping(value = {"/{menuId}/add-items"})
	public String addMenuItems(
	    @PathVariable("menuId") Long menuId,
	    @RequestParam("selectedItemIds") List<Long> selectedItemIds,
	    RedirectAttributes redirectAttributes) {

		logger.info("Adding items to menu ID " + menuId + ": " + selectedItemIds);
		try {
			Menu menu = menuService.get(menuId);
			if (menu == null) {
				logger.warn("Menu with ID " + menuId + " not found.");
				redirectAttributes.addFlashAttribute("errorMessage", "Menu not found.");
				return "redirect:/menu";
			}
			
			selectedItemIds.forEach(itemId -> {
				try {
					MenuItem menuItem =	menuItemService.get(itemId);
					if (menuItem != null && !menu.getMenuItems().contains(menuItem)) {
						menu.getMenuItems().add(menuItem);
					}
				} catch (ServiceException e) {
					logger.error("Error retrieving MenuItem with ID " + itemId, e);
				}
			});
			
			menuService.update(menu);
			redirectAttributes.addFlashAttribute("successMessage", "Menu items added successfully.");
		} catch (ServiceException e) {
			logger.error("Error adding items to menu ID " + menuId, e);
			redirectAttributes.addFlashAttribute("errorMessage", "Error adding menu items: " + e.getMessage());
		}
		return "redirect:/menu/" + menuId + "/edit";
	}
	
	@GetMapping(value = {"/{menuId}/view"})
	public String viewMenu(@PathVariable("menuId") Long menuId, ModelMap model) throws ServiceException {
		Menu menu = menuService.get(menuId);
		if (menu == null) {
			logger.warn("Menu with ID " + menuId + " not found.");
			return "redirect:/menu";
		}
		model.addAttribute("menu", menu);
		
		return "menu/view.html";
	}
	
	@Override
	public String getModule() {
		return "menu";
	}
}
