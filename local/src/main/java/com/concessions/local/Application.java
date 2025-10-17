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

import com.concessions.local.config.AppConfig;
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
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.controller.DeviceCodeController;
import com.concessions.local.ui.controller.SetupController;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.view.DeviceCodeDialog;
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
	private final TokenAuthService authService = new TokenAuthService();
	private final UserRestService userService = new UserRestService();

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
	protected MenuRepository menuRepository;

	@Autowired
	protected SetupController setupController;

	public static Organization selectedOrganization;

	public Application() {
	}

	public void initialize () {
		applicationModel.addPropertyChangeListener(applicationFrame);
		applicationModel.setTokenResponse(authService.loadTokenResponse());
		
		deviceCodeController.addDeviceCodeListener(new DeviceCodeController.DeviceCodeListener() {
			@Override
			public void onDeviceCodeAuthenticated (TokenResponse token) {
				applicationModel.setStatusMessage("Authenticated.");
				initializeMainApplication();
			}
		});
	}
		
	public void execute () {
		// Show the main application window
		SwingUtilities.invokeLater(() -> {
			applicationFrame.setVisible(true);
		});

		deviceCodeController.execute();
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
		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			
			context.register(JpaConfig.class);
			context.register(AppConfig.class);
            context.scan("com.concessions.local");
            context.refresh();
            context.registerShutdownHook();

			Application application = context.getBean(Application.class);	
			application.initialize();
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
