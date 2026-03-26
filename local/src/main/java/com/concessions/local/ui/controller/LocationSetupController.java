package com.concessions.local.ui.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Location;
import com.concessions.client.model.Menu;
import com.concessions.client.model.Organization;
import com.concessions.client.rest.LocationRestClient;
import com.concessions.client.rest.MenuRestClient;
import com.concessions.client.rest.OrganizationRestClient;
import com.concessions.client.service.MenuService;
import com.concessions.client.service.ServiceException;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.LocationConfigurationService;
import com.concessions.local.service.ServerConfigurationService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.LocationSetupPanel;
import com.concessions.local.ui.model.LocationSetupModel;

import jakarta.annotation.PostConstruct;

@Component
public class LocationSetupController {

	private static final Logger logger = LoggerFactory.getLogger(LocationSetupController.class);

	// The rest clients we need to talk to the server
	@Autowired
	protected OrganizationRestClient organizationClient;

	@Autowired
	protected LocationRestClient locationClient;

	@Autowired
	protected MenuRestClient menuClient;

	// The services we need
	@Autowired
	protected ApplicationConfigurationService appConfigService;

	@Autowired
	protected MenuService menuService;

	@Autowired
	protected ServerConfigurationService serverConfigService;

	@Autowired
	protected LocationConfigurationService locationConfigurationService;

	// The configurations we need
	@Autowired
	protected ApplicationConfiguration appConfig;

	@Autowired
	protected ServerConfiguration serverConfig;

	// The ui components we need
	@Autowired
	private ApplicationFrame applicationFrame;

	@Autowired
	protected ApplicationModel appModel;

	private LocationSetupModel model;
	
	private LocationSetupPanel view;

	public LocationSetupController() {
	}

	@PostConstruct
	private void initializeController() {
		model = new LocationSetupModel();
		view = new LocationSetupPanel(model);
		view.setupNavigationListeners(() -> {
			// Logic for 'Next'
			doLocationSetup();
		}, () -> {
			try {
				appConfigService.reset();
				serverConfigService.reset();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		});

		applicationFrame.addPanel(view, LocationSetupPanel.NAME);

		view.addOrganizationSelectionListener(e -> {
			Organization selectedOrganization = view.getSelectedOrganization();
			if (!LocationSetupPanel.PLEASE_SELECT.equals(selectedOrganization.toString())) {
				// set this because the multitenant discriminator needs this for future calls.
				appConfig.setOrganizationId(selectedOrganization.getId());
				logger.debug("Selected organization: " + selectedOrganization);
				loadLocations();
			}
		});
		view.addLocationSelectionListener(e -> {
			Location selectedLocation = view.getSelectedLocation();
			if (!LocationSetupPanel.PLEASE_SELECT.equals(selectedLocation.toString())) {
				loadMenus();
			}
		});
	}

	protected void doLocationSetup() {
		logger.info("Saving Location Configuration");
		Organization organization = view.getSelectedOrganization();
		Location location = view.getSelectedLocation();
		Menu menu = view.getSelectedMenu();
		try {
			menuService.create(menu);
		} catch (ServiceException ex) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to save menu: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		LocationConfiguration locConfig = new LocationConfiguration();
		locConfig.setOrganizationId(organization.getId());
		locConfig.setOrganizationName(organization.getName());
		locConfig.setLocationId(location.getId());
		locConfig.setLocationName(location.getName());
		locConfig.setMenuId(menu.getId());
		locConfig.setMenuName(menu.getName());
		appConfig.setLocationConfiguration(locConfig);
		try {
			appConfigService.save();
		} catch (BackingStoreException ex) {
			JOptionPane.showMessageDialog(applicationFrame, "Failed to save configuration: " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public void execute() {
		appModel.setStatus("Location Setup...");

		model.clear();
		loadOrganizations();

		SwingUtilities.invokeLater(() -> {
			applicationFrame.showPanel(LocationSetupPanel.NAME);
		});
	}

	public boolean isComplete() {
		return appConfig.isLocationConfigured();
	}

	protected void loadOrganizations() {
		CompletableFuture<List<Organization>> futureOrganizations = organizationClient.findAll();

		futureOrganizations.thenAccept(organizations -> {
			logger.debug("Organization data received in the background thread!");
			model.setOrganizations(organizations);
		}).exceptionally(ex -> {
			// Handle any exceptions that occurred during the remote call
			logger.error("Failed to fetch organizations: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(applicationFrame, "Error fetching organizations: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null; // Return null to complete the exceptional stage normally
		});
	}

	protected void loadLocations() {
		CompletableFuture<List<Location>> futureLocations = locationClient.findAll();

		futureLocations.thenAccept(locations -> {
			logger.debug("location data received in the background thread!");
			model.setLocations(locations);
		}).exceptionally(ex -> {
			// Handle any exceptions that occurred during the remote call
			logger.error("Failed to fetch locations: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(applicationFrame, "Error fetching locations: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null; // Return null to complete the exceptional stage normally
		});
	}

	protected void loadMenus() {
		CompletableFuture<List<Menu>> futureMenus = menuClient.findAll();

		futureMenus.thenAccept(menus -> {
			logger.debug("Menu data received in the background thread!");
			model.setMenus(menus);
		}).exceptionally(ex -> {
			// Handle any exceptions that occurred during the remote call
			logger.error("Failed to fetch menus: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(applicationFrame, "Error fetching menus: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null; // Return null to complete the exceptional stage normally
		});
	}
}
