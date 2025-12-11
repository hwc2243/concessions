package com.concessions.local.ui.controller;

import static com.concessions.local.base.Constants.LOCATION_CONFIGURATION_PREFERENCE;
import static com.concessions.local.base.Constants.PIN_PREFERENCE;

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
import com.concessions.common.service.PreferenceService;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.server.ServerApplication;
import com.concessions.local.service.LocationConfigurationService;
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
	protected LocationConfigurationService locationConfigurationService;

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
				
				String pin = view.getPin();
				String confirmPin = view.getConfirmPin();
				
				// 1. Validate PIN is purely numeric and not empty
				if (!isValidPin(pin)) {
					JOptionPane.showMessageDialog(view, "The Device PIN must be purely numeric and cannot be empty.", 
							"Validation Error", JOptionPane.ERROR_MESSAGE);
					return; // Stop processing and keep dialog open
				}
				
				// 2. Validate PIN and Confirm PIN match
				if (!pin.equals(confirmPin)) {
					JOptionPane.showMessageDialog(view, "The Device PIN and Confirm PIN do not match.", 
							"Validation Error", JOptionPane.ERROR_MESSAGE);
					return; // Stop processing and keep dialog open
				}
				
				Organization organization = view.getSelectedOrganization();
				Location location = view.getSelectedLocation();
				Menu menu = view.getSelectedMenu();
				logger.debug("organization: " + organization);
				logger.debug("location: " + location);
				logger.debug("menu: " + menu);
				LocationConfiguration organizationConfiguration = new LocationConfiguration();
				organizationConfiguration.setOrganizationId(organization.getId());
				organizationConfiguration.setOrganizationName(organization.getName());
				organizationConfiguration.setLocationId(location.getId());
				organizationConfiguration.setLocationName(location.getName());
				organizationConfiguration.setMenuId(menu.getId());
				organizationConfiguration.setMenuName(menu.getName());
				organizationConfiguration.setPin(Integer.parseInt(pin));
				
				try {
					organizationConfiguration = locationConfigurationService.create(organizationConfiguration);
					preferenceService.save(LOCATION_CONFIGURATION_PREFERENCE, String.valueOf(organizationConfiguration.getId()));
					preferenceService.save(PIN_PREFERENCE, pin);
					applicationModel.setPIN(pin);
					applicationModel.setLocationConfiguration(organizationConfiguration);
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

	private boolean isValidPin(String pin) {
        if (pin == null || pin.isEmpty()) {
            return false;
        }
        
        // Check if the pin contains only digits
        for (char c : pin.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
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

	protected void notifySetupCompleted (LocationConfiguration organizationConfiguration) {
		for (SetupListener listener : listeners) {
			listener.setupCompleted(organizationConfiguration);
		}
	}


	
	public interface SetupListener {
		void setupCompleted (LocationConfiguration organizationConfiguration);
	}
}
