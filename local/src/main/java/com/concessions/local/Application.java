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

import com.concessions.local.rest.LocationService;
import com.concessions.local.rest.MenuService;
import com.concessions.local.rest.OrganizationService;
import com.concessions.local.rest.UserService;
import com.concessions.local.service.QRGeneratorService;
import com.concessions.local.service.TokenAuthService;
import com.concessions.local.service.TokenAuthService.TokenResponse;
import com.concessions.local.ui.SetupDialog;
import com.concessions.model.Location;
import com.concessions.model.Menu;
import com.concessions.model.Organization;
import com.concessions.model.User;

public class Application extends JFrame {

	private final LocationService locationService = new LocationService();
	private final MenuService menuService = new MenuService();
	private final OrganizationService orgService = new OrganizationService();
	private final QRGeneratorService qrService = new QRGeneratorService();
	private final TokenAuthService authService;
	private final UserService userService = new UserService();
	
	private JLabel statusLabel;
	private JDialog modal;
	private SetupDialog orgDialog;

	public static TokenResponse tokenResponse;
	public static Organization selectedOrganization;
	
	public Application() {
		super("Concessions Management System");
		this.authService = new TokenAuthService();
		initializeUI();
		initializeAuthToken();
	}

	private JMenuBar initializeMenuBar() {
		JMenuBar menuBar = new javax.swing.JMenuBar();
		JMenu fileMenu = new javax.swing.JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		JMenuItem logoutItem = new javax.swing.JMenuItem("Logout");
		logoutItem.setMnemonic(KeyEvent.VK_L);
		fileMenu.add(logoutItem);

		return menuBar;
	}

	private void initializeAuthToken() {
		tokenResponse = authService.retrieveTokenResponse();
		if (tokenResponse == null) {
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

	private void initializeUI() {
		// Set up the main frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setLayout(new BorderLayout(5, 5));
		setJMenuBar(initializeMenuBar());
		
		orgDialog = new SetupDialog(this);
		orgDialog.addOrganizationSelectionListener(ev -> {
			selectedOrganization = (Organization)((JComboBox)ev.getSource()).getSelectedItem();
			System.out.println("Selected organization: " + (selectedOrganization != null ? selectedOrganization.getName() : "null"));
			try
			{
				List<Location> locations = locationService.findAll();
				System.out.println("Fetched " + locations.size() + " locations for organization " + selectedOrganization.getName());
				orgDialog.setLocations(locations);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error fetching locations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		orgDialog.addLocationSelectionListener(ev -> {
			Location selectedLocation = (Location)((JComboBox)ev.getSource()).getSelectedItem();
			System.out.println("Selected location: " + (selectedLocation != null ? selectedLocation.getName() : "null"));
			try
			{
				List<Menu> menus = menuService.findAll();
				System.out.println("Fetched " + menus.size() + " menus for location " + selectedLocation.getName());
				orgDialog.setMenus(menus);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error fetching menus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		// Initialize the Label (the content of the status bar)
		statusLabel = new JLabel("Starting authentication...");
		statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
		statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8)); // Add padding

		// 2. Create the Status Bar Panel (container for the label)
		JPanel statusBar = new JPanel(new BorderLayout());
		statusBar.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), // Top separator line
						BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		statusBar.setBackground(Color.WHITE);

		// 3. Add the label to the status bar panel (aligned LEFT by default in
		// BorderLayout)
		statusBar.add(statusLabel, BorderLayout.WEST);

		// 4. Add the status bar panel to the bottom of the JFrame
		add(statusBar, BorderLayout.SOUTH);

		// --- Main Content (Placeholder) ---
		// Placing a placeholder panel in the CENTER region
		// to show where your main working area or QR code display would go.
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.add(new JLabel("Welcome to the Concession Management System"));
		add(mainContentPanel, BorderLayout.CENTER);
		
		// Center the frame on the screen
		setLocationRelativeTo(null);
		setVisible(true);
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
					statusLabel.setText("Authenticated! Token acquired.");
					System.out.println("Access Token received: " + tokenResponse.access_token());
					JOptionPane.showMessageDialog(Application.this, "Login Successful!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					// Close the modal if it's still open
					if (modal != null) {
						modal.dispose();
					}

					authService.storeTokenResponse(tokenResponse);
					Application.this.tokenResponse = tokenResponse;

					// TODO: Initialize main application logic here
					initializeMainApplication();

				} catch (Exception ex) {
					// Handle join() exceptions and nested exceptions
					Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
					statusLabel.setText("Authentication Failed.");
					JOptionPane.showMessageDialog(Application.this, "Authentication Failed: " + cause.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);

					// Close the modal if it's still open
					if (modal != null) {
						modal.dispose();
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
	private void showDeviceCodeModal(TokenAuthService.DeviceCodeResponse response) {
		modal = new JDialog(this, "Authentication Required", Dialog.ModalityType.APPLICATION_MODAL);
		modal.setLayout(new BorderLayout(10, 10));
		modal.setSize(500, 500);
		modal.setLocationRelativeTo(this);
		modal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // User can dismiss the modal

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel title = new JLabel("Please Authorize This Device", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 16));
		contentPanel.add(title);
		contentPanel.add(Box.createVerticalStrut(10));

		String verificationUri = response.verification_uri() + "?user_code=" + response.user_code();
		ImageIcon qrIcon = qrService.generateQRCode(verificationUri, 200, 200);
		if (qrIcon != null) {
			JLabel qrLabel = new JLabel(qrIcon);
			qrLabel.setAlignmentX(CENTER_ALIGNMENT);
			contentPanel.add(qrLabel);
			contentPanel.add(Box.createVerticalStrut(10));
		}

		// 1. Verification URI link
		JLabel uriLabel = new JLabel("<html><p>Open this URL in your browser:</p></html>");
		contentPanel.add(uriLabel);
		JTextPane uriText = new JTextPane();
		uriText.setText(response.verification_uri());
		uriText.setEditable(false);
		uriText.setBackground(Color.LIGHT_GRAY);
		uriText.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uriText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				try {
					// Automatically open the link in the default system browser
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						Desktop.getDesktop().browse(new java.net.URI(response.verification_uri()));
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(modal, "Could not open browser: " + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPanel.add(uriText);
		contentPanel.add(Box.createVerticalStrut(15));

		// 2. User Code
		JLabel codeLabel = new JLabel("<html><p>Then enter this code:</p></html>");
		contentPanel.add(codeLabel);
		JTextPane codeText = new JTextPane();
		codeText.setText(response.user_code());
		codeText.setFont(new Font("Monospaced", Font.BOLD, 24));
		codeText.setEditable(false);
		codeText.setForeground(new Color(0, 100, 0));
		codeText.setBackground(Color.WHITE);
		contentPanel.add(codeText);
		contentPanel.add(Box.createVerticalStrut(10));

		// Timer/Instruction
		JLabel info = new JLabel("This code will expire in " + (response.expires_in() / 60) + " minutes.",
				SwingConstants.CENTER);
		contentPanel.add(info);

		modal.add(contentPanel, BorderLayout.CENTER);

		// Cancel button in the modal's footer
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			modal.dispose();
			statusLabel.setText("Login cancelled by user.");
			// TODO: If necessary, stop the polling scheduler in TokenAuthService
		});
		buttonPanel.add(cancelButton);
		modal.add(buttonPanel, BorderLayout.SOUTH);

		modal.setVisible(true);
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
				statusLabel.setText("Setup.");
				orgDialog.setOrganizations(result instanceof String ? new ArrayList<>() : (ArrayList<Organization>) result);
				orgDialog.setVisible(true);
			});
		});
	}

	public Organization getSelectedOrganization() {
		return selectedOrganization;
	}
	
	public void setSelectedOrganization(Organization org) {
		this.selectedOrganization = org;
		try {
		  List<Location> locations = locationService.findAll();
		  System.out.println("Fetched " + locations.size() + " locations for organization " + org.getName());
		  locations.forEach(loc -> System.out.println(loc.getName()));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error fetching locations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		// Launch all Swing components on the Event Dispatch Thread (EDT)
		SwingUtilities.invokeLater(Application::new);
	}
}
