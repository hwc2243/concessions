package com.concessions.local.server;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Menu;
import com.concessions.client.rest.MenuRestClient;
import com.concessions.client.service.MenuService;
import com.concessions.local.config.AppConfig;
import com.concessions.local.config.JpaConfig;
import com.concessions.local.model.OrganizationConfiguration;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.service.OrganizationConfigurationService;
import com.concessions.local.service.PreferenceService;
import com.concessions.local.ui.AboutDialog;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.JournalCloseAction;
import com.concessions.local.ui.action.JournalStartAction;
import com.concessions.local.ui.action.JournalSuspendAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.controller.DeviceCodeController;
import com.concessions.local.ui.controller.JournalController;
import com.concessions.local.ui.controller.JournalListener;
import com.concessions.local.ui.controller.SetupController;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.view.DeviceCodeDialog;

import jakarta.annotation.PostConstruct;

@Component
public class ServerApplication implements PropertyChangeListener {

	@Value("${application.name:Concessions Management System}")
	protected String applicationName;
	
	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;
	
	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected ApplicationModel applicationModel;
	
	@Autowired
	protected DeviceCodeDialog deviceCodeModal;
	
	@Autowired
	protected DeviceCodeController deviceCodeController;
	
	@Autowired
	protected JournalCloseAction journalCloseAction;
	
	@Autowired
	protected JournalStartAction journalStartAction;
	
	@Autowired
	protected JournalSuspendAction journalSuspendAction;
	
	@Autowired
	protected JournalController journalController;
	
	@Autowired
	protected ExitAction exitAction;
	
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
	protected MenuService menuService;
	
	@Autowired
	protected OrganizationConfigurationService organizationConfigurationService;
	
	@Autowired
	protected PreferenceService preferenceService;
	
	@Autowired
	protected SetupController setupController;

	@Autowired
	protected TokenAuthService authService;

	public ServerApplication() {
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
		
		journalController.addJournalListener(new JournalListener() {
			
			@Override
			public void journalStarted(Journal journal) {
				orderAction.setEnabled(false);
			}

			@Override
			public void journalOpened(Journal journal) {
				orderAction.setEnabled(true);
				journalCloseAction.setEnabled(true);
				journalSuspendAction.setEnabled(true);
			}

			@Override
			public void journalClosed(Journal journal) {
				orderAction.setEnabled(false);
			}

			@Override
			public void journalSuspended(Journal journal) {
				orderAction.setEnabled(false);
				
			}
			
			public void journalSynced (Journal journal) {
				// HWC TODO can't think of anything to do at the moment
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
			setupDesktopHandler(applicationFrame);

			applicationFrame.setVisible(true);
		});
		
		// if we have an organizationConfiguration it doesn't matter if we are authenticated
		String organizationConfigurationIdText = preferenceService.get(ServerApplication.class, "organizationConfigurationId");
		if (organizationConfigurationIdText == null) {
			if (applicationModel.getTokenResponse() == null || !authService.isTokenValid(applicationModel.getTokenResponse())) {
				executeDeviceCode();
			} else {
				// it is possible we have good token but no organizationConfiguration so we should run executeSetup
				executeSetup(null);
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
			organizationConfigurationIdText = preferenceService.get(ServerApplication.class, "organizationConfigurationId");
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
	}
	
	private void executeSales (OrganizationConfiguration organizationConfiguration) {
		try
		{
			Menu menu = menuService.get(organizationConfiguration.getMenuId());
			if (menu == null) {
				menu = menuRestClient.get(organizationConfiguration.getMenuId()).get();
				menuService.create(menu);
			}
			applicationModel.setMenu(menu);
			journalStartAction.setEnabled(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void setupDesktopHandler (JFrame ownerFrame) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                desktop.setAboutHandler(e -> {
                    showAboutDialog(ownerFrame);
                });
            }
            if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
                desktop.setQuitHandler(new QuitHandler() {
                    @Override
                    public void handleQuitRequestWith(QuitEvent e, java.awt.desktop.QuitResponse response) {
                        // Trigger the ExitAction when the user selects "Quit" from the macOS menu.
                        exitAction.actionPerformed(new ActionEvent(
                            applicationFrame, 
                            ActionEvent.ACTION_PERFORMED, 
                            "macOS_Quit_Menu"
                        ));
                        
                        // Since ExitAction calls System.exit(0), we instruct the OS to proceed.
                        response.performQuit(); 
                    }
                });
            }
        }
    }
	
	private void showAboutDialog(JFrame ownerFrame) {
        AboutDialog.showAboutDialog(ownerFrame, applicationName, applicationVersion);
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
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			try {
				System.setProperty("apple.awt.application.name", "Concessions");
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			    UIManager.setLookAndFeel(new FlatLightLaf());
			} catch( Exception ex ) {
			    System.err.println( "Failed to initialize LaF" );
			}
		}

		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			ConfigurableEnvironment environment = context.getEnvironment();

	        // Load application.yml from classpath
	        environment.getPropertySources().addLast(
	                new ResourcePropertySource("application.yml", "classpath:application.yml"));
			
			context.register(JpaConfig.class);
			context.register(AppConfig.class);
            context.scan("com.concessions.local", "com.concessions.client");
            context.refresh();
            context.registerShutdownHook();

			ServerApplication application = context.getBean(ServerApplication.class);	
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
