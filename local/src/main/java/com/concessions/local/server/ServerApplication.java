package com.concessions.local.server;

import static com.concessions.local.base.Constants.DEVICE_ID_PREFERENCE;
import static com.concessions.local.base.Constants.LOCATION_CONFIGURATION_PREFERENCE;
import static com.concessions.local.base.Constants.PIN_PREFERENCE;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.UUID;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Menu;
import com.concessions.client.rest.MenuRestClient;
import com.concessions.client.service.MenuService;
import com.concessions.common.event.JournalListener;
import com.concessions.common.event.JournalNotifier;
import com.concessions.common.network.JournalClientHandler;
import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.common.service.PreferenceService;
import com.concessions.dto.JournalDTO;
import com.concessions.local.base.AbstractApplication;
import com.concessions.local.base.ui.AboutDialog;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ApplicationConfiguration.ApplicationRole;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.dto.MenuMapper;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.model.Device;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.server.config.AppConfig;
import com.concessions.local.server.config.JpaConfig;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.server.orchestrator.OrderOrchestrator;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.DeviceService;
import com.concessions.local.service.LocationConfigurationService;
import com.concessions.local.service.ServiceException;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.SystemSetupPanel;
import com.concessions.local.ui.WelcomePanel;
import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.JournalCloseAction;
import com.concessions.local.ui.action.JournalStartAction;
import com.concessions.local.ui.action.JournalSuspendAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.controller.DeviceCodeController;
import com.concessions.local.ui.controller.JournalController;
import com.concessions.local.ui.controller.SetupController;

import jakarta.annotation.PostConstruct;

@Component
public class ServerApplication extends AbstractApplication implements PropertyChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
	
	@Value("${application.name:Concessions Management System}")
	protected String applicationName;
	
	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;
	
	@Autowired
	protected ApplicationConfigurationService appConfigService;
	
	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected ApplicationModel appModel;

	@Autowired
	protected DeviceCodeController deviceCodeController;

	private ApplicationConfiguration appConfig;
	
	private ServerConfiguration serverConfig;
	
	// HWC This is the old stuff, needs to be refactored
	@Autowired
	protected ServerApplicationModel applicationModel;
	
	@Autowired
	protected DeviceService deviceService;
	
	protected JournalCloseAction journalCloseAction;
	
	protected JournalStartAction journalStartAction;
	
	protected JournalSuspendAction journalSuspendAction;
	
	protected JournalController journalController;
	
	@Autowired
	protected JournalNotifier journalNotifier;
	
	@Autowired
	protected ExitAction exitAction;
	
	@Autowired
	protected LoginAction loginAction;
	
	@Autowired
	protected LogoutAction logoutAction;
	
	@Autowired
	protected SetupAction setupAction;
	
	@Autowired
	protected MenuRestClient menuRestClient;
	
	@Autowired
	protected MenuService menuService;
	
	@Autowired
	protected LocationConfigurationService locationConfigurationService;
	
	@Autowired
	protected OrderOrchestrator orderOrchestrator;
	
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

		
		SwingUtilities.invokeLater(() -> {
			setupDesktopHandler(applicationFrame);
			WelcomePanel welcomePanel = new WelcomePanel(applicationName, applicationVersion);
			applicationFrame.addPanel(welcomePanel, WelcomePanel.NAME);
			applicationFrame.showPanel(WelcomePanel.NAME);
			
			applicationFrame.setVisible(true);
		});

		appConfig = appConfigService.get();
		appConfig.addPropertyChangeListener(applicationFrame);
		appConfig.addPropertyChangeListener(this);
		
		applicationFrame.addPanel(new SystemSetupPanel(role -> {
			try {
				logger.info("Setting application role to: {}", role);
				appConfig.setApplicationRole(role);
				appConfigService.save();
			} catch (BackingStoreException ex) {
				logger.error("Failed to save application configuration", ex);
				JOptionPane.showMessageDialog(applicationFrame, "Error saving configuration.");
				appConfig.setApplicationRole(ApplicationRole.UNDECIDED);
			}
		}), SystemSetupPanel.NAME);
		
		/*
		// register our deviceId
		String deviceId = preferenceService.get(DEVICE_ID_PREFERENCE);
		if (StringUtils.isBlank(deviceId)) {
			deviceId = UUID.randomUUID().toString();
			Device device = new Device();
			device.setDeviceId(deviceId);
			device.setDeviceType(DeviceTypeType.SERVER);
			try {
				deviceService.create(device);
				logger.info("Registered device {} as server", deviceId);
			} catch (ServiceException e) {
				logger.error("Failed to register device {} as server", deviceId);
				System.exit(1);
			}
			try {
				preferenceService.save(DEVICE_ID_PREFERENCE, deviceId);
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
		
		String pin = preferenceService.get(PIN_PREFERENCE);
		if (StringUtils.isNotBlank(pin)) {
			applicationModel.setPIN(pin);
		}

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
		
		journalNotifier.addJournalListener(new JournalListener() {
			
			@Override
			public void journalStarted(JournalDTO journal) {
			}

			@Override
			public void journalChanged (JournalDTO journal) {
			}
			
			@Override
			public void journalOpened(JournalDTO journal) {
				journalCloseAction.setEnabled(true);
				journalSuspendAction.setEnabled(true);
				journalChange(journal);
			}

			@Override
			public void journalClosed(JournalDTO journal) {
				journalChange(journal);
			}

			@Override
			public void journalSuspended(JournalDTO journal) {
				journalChange(journal);
			}
			
			public void journalSynced (JournalDTO journal) {
			}
			
			protected void journalChange (JournalDTO journal) {
				List<Device> posDevices = deviceService.findByDeviceType(DeviceTypeType.POS);
				for (Device device : posDevices) {
					try {
						if (StringUtils.isNotBlank(device.getDeviceIp()) && device.getDevicePort() > 0) {
							messenger.sendRequest(device.getDeviceIp(), device.getDevicePort(), NetworkConstants.JOURNAL_SERVICE, NetworkConstants.JOURNAL_CHANGE_ACTION, journal, SimpleResponseDTO.class);
						}
					} catch (MessengerException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		setupController.addSetupListener(new SetupController.SetupListener() {
			
			@Override
			public void setupCompleted(LocationConfiguration organizationConfiguration) {
				executeSales(organizationConfiguration);
			}
		});
		*/
	}
		
	public void execute () {
		logger.info("Starting Server application");

		if (appConfig.getApplicationRole() == ApplicationRole.UNDECIDED) {
			appModel.setStatus("Setup...");
			executeSystemSetup();
		} else if (appConfig.getApplicationRole() == ApplicationRole.SERVER) {
			executeServerSetup();
		}
		
		/*
		// if we have an organizationConfiguration it doesn't matter if we are authenticated
		String organizationConfigurationIdText = preferenceService.get(LOCATION_CONFIGURATION_PREFERENCE);
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
		*/
	}
	
	private void executeSystemSetup () {
		showPanel(SystemSetupPanel.NAME);
	}
	
	private void executeServerSetup () {
		deviceCodeController.execute();
	}

	private void executeSetup (String organizationConfigurationIdText) {
		if (organizationConfigurationIdText == null) {
			organizationConfigurationIdText = preferenceService.get(LOCATION_CONFIGURATION_PREFERENCE);
		}
		
		LocationConfiguration organizationConfiguration = null;
		if (organizationConfigurationIdText != null) {
			long organizationConfigurationId = Long.parseLong(organizationConfigurationIdText);
			try {
				organizationConfiguration = locationConfigurationService.get(organizationConfigurationId);
				if (organizationConfiguration != null) {
					applicationModel.setLocationConfiguration(organizationConfiguration);
					applicationModel.setOrganizationId(organizationConfiguration.getOrganizationId());
					applicationModel.setStatusMessage("Ready");
					executeSales(organizationConfiguration);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (organizationConfiguration == null) {
			setupController.execute();
		}
	}
	
	private void executeSales (LocationConfiguration organizationConfiguration) {
		try
		{
			Menu menu = menuService.get(organizationConfiguration.getMenuId());
			if (menu == null) {
				menu = menuRestClient.get(organizationConfiguration.getMenuId()).get();
				menuService.create(menu);
			}
			applicationModel.setMenu(MenuMapper.toDto(menu));
			journalController.initialize();
			journalStartAction.setEnabled(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected boolean performQuit ()
	{
        // Trigger the ExitAction when the user selects "Quit" from the macOS menu.
        exitAction.actionPerformed(new ActionEvent(
            applicationFrame, 
            ActionEvent.ACTION_PERFORMED, 
            "macOS_Quit_Menu"
        ));
        
        return false;
	}
	
	protected void showAboutDialog(JFrame ownerFrame) {
        AboutDialog.showAboutDialog(ownerFrame, applicationName, applicationVersion);
	}
	
	protected void showPanel (String name) {
		SwingUtilities.invokeLater(() -> {
			applicationFrame.showPanel(name);
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == appConfig) {
			if (evt.getPropertyName().equals(ApplicationConfiguration.PROPERTY_APPLICATION_ROLE)) {
				switch ((ApplicationRole)evt.getNewValue()) {
					case UNDECIDED:
						executeSystemSetup();
						break;
					case SERVER:
						executeServerSetup();
						break;
					case CLIENT:
						break;
				}
			}
		}
		
		if (ServerApplicationModel.CONNECTED.equals(evt.getPropertyName()) ||
				ServerApplicationModel.TOKEN_RESPONSE.equals(evt.getPropertyName())) {
			setupAction.setEnabled(applicationModel.isConnected() && serverConfig.getTokenResponse() != null);
			loginAction.setEnabled(applicationModel.isConnected() && serverConfig.getTokenResponse() == null);
			logoutAction.setEnabled(serverConfig.getTokenResponse() != null);
		} else if (ServerApplicationModel.JOURNAL.equals(evt.getPropertyName())) {
			orderOrchestrator.initialize((JournalDTO)evt.getNewValue());
		}
	}

	public static void main(String[] args) {
		initializeLaF("CMS Server");

		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			ConfigurableEnvironment environment = context.getEnvironment();

			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
			ClassPathResource resource = new ClassPathResource("application-server.yml");
			PropertySource<?> yamlPropertySource = loader.load("application-server.yml", resource)
			                                            .get(0); // Take the first (and usually only) document
	        environment.getPropertySources().addLast(yamlPropertySource);
			
			context.register(JpaConfig.class);
			context.register(AppConfig.class);
            // context.scan("com.concessions.local", "com.concessions.client", "com.concessions.common");
            context.refresh();
            context.registerShutdownHook();

			ServerApplication application = context.getBean(ServerApplication.class);	
			application.execute();


		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start Server application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			if (context != null)
			{
				context.close();
			}
			System.exit(1);
		}
	}
}
