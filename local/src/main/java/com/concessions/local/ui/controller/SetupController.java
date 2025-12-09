package com.concessions.local.ui.controller;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import com.concessions.local.model.OrganizationConfiguration;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.server.Application;
import com.concessions.local.service.OrganizationConfigurationService;
import com.concessions.local.service.PreferenceService;
import com.concessions.local.service.ServiceException;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.model.SetupModel;
import com.concessions.local.ui.view.SetupDialog;

import jakarta.annotation.PostConstruct;

@Component
public class SetupController {

	private static final Logger logger = LoggerFactory.getLogger(SetupController.class);
	
	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected OrganizationRestClient organizationClient;

	@Autowired
	protected LocationRestClient locationClient;

	@Autowired
	protected MenuRestClient menuClient;
	
	@Autowired
	protected OrganizationConfigurationService organizationConfigurationService;

	@Autowired
	protected PreferenceService preferenceService;

	
	@Autowired
	private ApplicationFrame applicationFrame;

	private SetupModel model;
	private SetupDialog view;
	
	private List<SetupListener> listeners = new java.util.ArrayList<>();


	public SetupController() {
	}

	@PostConstruct
	private void initializeController() {
		model = new SetupModel();
		view = new SetupDialog(model);

		view.addOrganizationSelectionListener(e -> {
			Organization selectedOrganization = view.getSelectedOrganization();
			if (!SetupDialog.PLEASE_SELECT.equals(selectedOrganization.toString())) {
				applicationModel.setOrganizationId(selectedOrganization.getId());
				logger.debug("Selected organization: " + selectedOrganization);
				loadLocations();
			}
		});
		view.addLocationSelectionListener(e -> {
			Location selectedLocation = view.getSelectedLocation();
			if (!SetupDialog.PLEASE_SELECT.equals(selectedLocation.toString())) {
				loadMenus();
			}
		});
		view.addMenuSelectionListener(e -> {
			Menu selectedMenu = view.getSelectedMenu();
			if (!SetupDialog.PLEASE_SELECT.equals(selectedMenu.toString())) {
				view.setSetupEnabled(true);
			}
		});
		view.addActionListener(e -> {
			switch (e.getActionCommand()) {
			case OK_COMMAND:
				Organization organization = view.getSelectedOrganization();
				Location location = view.getSelectedLocation();
				Menu menu = view.getSelectedMenu();
				logger.debug("organization: " + organization);
				logger.debug("location: " + location);
				logger.debug("menu: " + menu);
				OrganizationConfiguration organizationConfiguration = new OrganizationConfiguration();
				organizationConfiguration.setOrganizationId(organization.getId());
				organizationConfiguration.setOrganizationName(organization.getName());
				organizationConfiguration.setLocationId(location.getId());
				organizationConfiguration.setLocationName(location.getName());
				organizationConfiguration.setMenuId(menu.getId());
				organizationConfiguration.setMenuName(menu.getName());
				try {
					organizationConfiguration = organizationConfigurationService.create(organizationConfiguration);
					preferenceService.save(Application.class, "organizationConfigurationId", String.valueOf(organizationConfiguration.getId()));
					applicationModel.setOrganizationConfiguration(organizationConfiguration);
					notifySetupCompleted(organizationConfiguration);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case CANCEL_COMMAND:
				break;
			}
			view.setVisible(false);
		});

		view.setSetupEnabled(false);
	}

	public void execute() {
		applicationModel.setStatusMessage("Setup...");

		loadOrganizations();

		SwingUtilities.invokeLater(() -> {
			view.setLocationRelativeTo(applicationFrame);
			view.pack();
			view.setVisible(true);
		});
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
	
	public void addSetupListener(SetupListener listener) {
		listeners.add(listener);
	}

	public void removeSetupListener(SetupListener listener) {
		listeners.remove(listener);
	}

	protected void notifySetupCompleted (OrganizationConfiguration organizationConfiguration) {
		for (SetupListener listener : listeners) {
			listener.setupCompleted(organizationConfiguration);
		}
	}


	
	public interface SetupListener {
		void setupCompleted (OrganizationConfiguration organizationConfiguration);
	}
}
