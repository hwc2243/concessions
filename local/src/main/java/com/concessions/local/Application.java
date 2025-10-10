package com.concessions.local;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.concessions.local.config.JpaConfig;
import com.concessions.local.persistence.MenuRepository;
import com.concessions.local.rest.LocationRestService;
import com.concessions.local.rest.MenuRestService;
import com.concessions.local.rest.OrganizationRestService;
import com.concessions.local.rest.UserRestService;
import com.concessions.local.service.QRGeneratorService;
import com.concessions.local.service.TokenAuthService;
import com.concessions.local.service.TokenAuthService.TokenResponse;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.DeviceCodeModal;
import com.concessions.local.ui.controller.SetupController;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.view.SetupDialog;
import com.concessions.model.Location;
import com.concessions.model.Menu;
import com.concessions.model.Organization;
import com.concessions.model.User;

@Component
public class Application {

	private final LocationRestService locationService = new LocationRestService();
	private final MenuRestService menuService = new MenuRestService();
	private final OrganizationRestService orgService = new OrganizationRestService();
	private final QRGeneratorService qrService = new QRGeneratorService();
	private final TokenAuthService authService = new TokenAuthService();
	private final UserRestService userService = new UserRestService();

	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected DeviceCodeModal deviceCodeModal;
	
	@Autowired
	protected SetupController setupController;
	
	@Autowired
	protected MenuRepository menuRepository;

	public static TokenResponse tokenResponse;
	
	public static Organization selectedOrganization;

	public Application() {
	}

	public void initialize () {
		applicationModel.addPropertyChangeListener(applicationFrame);
	}
		
	public void execute () {
		// Show the main application window
		SwingUtilities.invokeLater(() -> {
			applicationFrame.setVisible(true);
		});
		
		// Start authentication process
		initializeAuthToken();
	}
	
	protected void initializeAuthToken() {
		tokenResponse = authService.loadTokenResponse();
		if (tokenResponse == null) {
			applicationModel.setStatusMessage("Starting authentication...");
			initiateLoginFlow();
		} else {
			System.out.println("refresh token " + tokenResponse.refresh_token());
			System.out.println("Using stored access token, expires in " + tokenResponse.expires_in() + " seconds.");
			if (tokenResponse.expires_in() < 60) {
				System.out.println("refreshing token...");
				authService.refreshToken(tokenResponse.refresh_token()).thenAccept(newToken -> {
					tokenResponse = newToken;
					authService.storeTokenResponse(tokenResponse);
					initializeMainApplication();
				}).exceptionally(ex -> {
					// Refresh failed (e.g., refresh token expired). Force new device login.
					initiateLoginFlow();
					return null;
				});
			} else {
				initializeMainApplication();
			}
		}
	}

	private void initiateLoginFlow() {
		// SwingWorker runs network/blocking code on a separate background thread
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			TokenResponse tokenResponse = null;

			@Override
			protected Void doInBackground() throws Exception {
				// 1. Request Device Code (Blocking the worker thread, not the EDT)
				TokenAuthService.DeviceCodeResponse response = authService.requestDeviceCode().join();

				// 2. Display the modal with instructions (must run on the EDT)
				SwingUtilities.invokeLater(() -> showDeviceCodeModal(response));

				// 3. Poll for Token (Blocks the worker thread until authorization completes)
				tokenResponse = authService.pollForToken(response).join();
				return null;
			}

			@Override
			protected void done() {
				// This method runs on the EDT, safe for UI updates
				try {
					get(); // This retrieves the result or re-throws exceptions from doInBackground()
					applicationModel.setStatusMessage("Authenticated! Token acquired.");
					System.out.println("Access Token received: " + tokenResponse.access_token());
					JOptionPane.showMessageDialog(Application.this.applicationFrame, "Login Successful!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					// Close the modal if it's still open
					if (deviceCodeModal.isVisible()) {
						deviceCodeModal.setVisible(false);
					}

					authService.storeTokenResponse(tokenResponse);
					Application.this.tokenResponse = tokenResponse;

					// TODO: Initialize main application logic here
					initializeMainApplication();

				} catch (Exception ex) {
					// Handle join() exceptions and nested exceptions
					Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
					applicationModel.setStatusMessage("Authentication Failed.");
					JOptionPane.showMessageDialog(Application.this.applicationFrame, "Authentication Failed: " + cause.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);

					// Close the modal if it's still open
					if (deviceCodeModal.isVisible()) {
						deviceCodeModal.setVisible(false);
					}
				}
			}
		};
		worker.execute();
	}

	/**
	 * Creates and displays a modal dialog with the Keycloak user code and
	 * verification URI.
	 */
	private void showDeviceCodeModal (TokenAuthService.DeviceCodeResponse response) {
		deviceCodeModal.showDialog(response.verification_uri(), response.user_code(), response.expires_in());
	}

	private void initializeMainApplication() {

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
			// Update UI (display modal) on the EDT
			SwingUtilities.invokeLater(() -> {
				applicationModel.setStatusMessage("Setup.");
				});
			setupController.execute();
		});
	}

	public static void main(String[] args) {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {

			context.register(JpaConfig.class);
            context.scan("com.concessions.local");
            context.refresh();

			Application application = context.getBean(Application.class);	
			application.initialize();
			application.execute();


		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}
