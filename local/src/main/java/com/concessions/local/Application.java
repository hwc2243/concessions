package com.concessions.local;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.concessions.local.config.AppConfig;
import com.concessions.local.config.JpaConfig;
import com.concessions.local.model.OrganizationConfiguration;
import com.concessions.local.rest.MenuRestClient;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.service.OrganizationConfigurationService;
import com.concessions.local.service.PreferenceService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.controller.DeviceCodeController;
import com.concessions.local.ui.controller.SetupController;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.view.DeviceCodeDialog;

import jakarta.annotation.PostConstruct;

@Component
public class Application implements PropertyChangeListener {

	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected DeviceCodeDialog deviceCodeModal;
	
	@Autowired
	protected DeviceCodeController deviceCodeController;
	
	@Autowired
	protected LoginAction loginAction;
	
	@Autowired
	protected LogoutAction logoutAction;
	
	@Autowired
	protected OrderAction orderAction;
	
	@Autowired
	protected SetupAction setupAction;
	
	@Autowired
	protected MenuRestClient menuRestClient;
	
	@Autowired
	protected OrganizationConfigurationService organizationConfigurationService;
	
	@Autowired
	protected PreferenceService preferenceService;
	
	@Autowired
	protected SetupController setupController;

	@Autowired
	protected TokenAuthService authService;

	public Application() {
	}

	@PostConstruct
	protected void initialize () {
		applicationModel.setTokenResponse(authService.loadTokenResponse());
		applicationModel.addPropertyChangeListener(applicationFrame);
		applicationModel.addPropertyChangeListener(this);
		
		deviceCodeController.addDeviceCodeListener(new DeviceCodeController.DeviceCodeListener() {
			@Override
			public void onDeviceCodeAuthenticated (TokenResponse token) {
				applicationModel.setStatusMessage("Authenticated");
				executeSetup(null);
			}
			
			public void onDeviceCodeFailed () {
				applicationModel.setStatusMessage("Authentication failed.");
			}
		});
		
		setupController.addSetupListener(new SetupController.SetupListener() {
			
			@Override
			public void setupCompleted(OrganizationConfiguration organizationConfiguration) {
				executeSales(organizationConfiguration);
			}
		});
	}
		
	public void execute () {
		// Show the main application window
		SwingUtilities.invokeLater(() -> {
			applicationFrame.setVisible(true);
		});
		
		// if we have an organizationConfiguration it doesn't matter if we are authenticated
		String organizationConfigurationIdText = preferenceService.get(Application.class, "organizationConfigurationId");
		if (organizationConfigurationIdText == null) {
			if (applicationModel.getTokenResponse() == null || !authService.isTokenValid(applicationModel.getTokenResponse())) {
				executeDeviceCode();
			}
		} else {
			executeSetup(organizationConfigurationIdText);
		}
	}
	
	private void executeDeviceCode () {
		deviceCodeController.execute();
	}

	private void executeSetup (String organizationConfigurationIdText) {
		if (organizationConfigurationIdText == null) {
			organizationConfigurationIdText = preferenceService.get(Application.class, "organizationConfigurationId");
		}
		if (organizationConfigurationIdText != null) {
			long organizationConfigurationId = Long.parseLong(organizationConfigurationIdText);
			try {
				OrganizationConfiguration organizationConfiguration = organizationConfigurationService.get(organizationConfigurationId);
				applicationModel.setOrganizationConfiguration(organizationConfiguration);
				applicationModel.setOrganizationId(organizationConfiguration.getOrganizationId());
				applicationModel.setStatusMessage("Ready");
				executeSales(organizationConfiguration);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		else {
			setupController.execute();
		}

		/*
		// Use a background thread (CompletableFuture) for the network operation
		CompletableFuture.supplyAsync(() -> {
			try {
				User user = userService.me();
				System.out.println("Logged in as: " + user.getUsername());
				Set<Organization> organizations = new HashSet<>();
				if (user.getOrganization() != null) {
					organizations.add(user.getOrganization());
				}
				if (user.getOrganizations() != null) {
					organizations.addAll(user.getOrganizations());
				}
				return new ArrayList<>(organizations);
			} catch (Exception e) {
				// Return error message for display
				return "Error fetching data: " + e.getMessage();
			}
		}).thenAccept(result -> {
			setupController.execute();
		});
		*/
	}
	
	private void executeSales (OrganizationConfiguration organizationConfiguration) {
		try
		{
			applicationModel.setMenu(menuRestClient.get(organizationConfiguration.getMenuId()).get());
			orderAction.setEnabled(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ApplicationModel.CONNECTED.equals(evt.getPropertyName()) ||
				ApplicationModel.TOKEN_RESPONSE.equals(evt.getPropertyName())) {
			setupAction.setEnabled(applicationModel.isConnected() && applicationModel.getTokenResponse() != null);
			loginAction.setEnabled(applicationModel.isConnected() && applicationModel.getTokenResponse() == null);
			logoutAction.setEnabled(applicationModel.getTokenResponse() != null);
		}
		
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			
			context.register(JpaConfig.class);
			context.register(AppConfig.class);
            context.scan("com.concessions.local");
            context.refresh();
            context.registerShutdownHook();

			Application application = context.getBean(Application.class);	
			application.execute();


		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			if (context != null)
			{
				context.close();
			}
			System.exit(1);
		}
	}
}
