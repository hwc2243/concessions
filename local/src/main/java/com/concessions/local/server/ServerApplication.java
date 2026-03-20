package com.concessions.local.server;

import static com.concessions.local.base.Constants.DEVICE_ID_PREFERENCE;
import static com.concessions.local.base.Constants.LOCATION_CONFIGURATION_PREFERENCE;
import static com.concessions.local.base.Constants.PIN_PREFERENCE;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.concessions.local.journal.action.JournalCloseAction;
import com.concessions.local.journal.action.JournalStartAction;
import com.concessions.local.journal.action.JournalSuspendAction;
import com.concessions.local.dto.DeviceTypeType;
import com.concessions.local.model.Device;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.network.LocalNetworkListener;
import com.concessions.local.network.client.JournalClientHandler;
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
import com.concessions.local.service.ServerConfigurationService;
import com.concessions.local.service.ServiceException;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.SystemSetupPanel;
import com.concessions.local.ui.WelcomePanel;
import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.controller.DeviceCodeController;
import com.concessions.local.ui.controller.JournalController;
import com.concessions.local.ui.controller.PINController;
import com.concessions.local.ui.controller.LocationSetupController;

import jakarta.annotation.PostConstruct;

@Component
public class ServerApplication extends AbstractApplication implements PropertyChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

	private static final int UNKNOWN = 0;
	private static final int SYSTEM_SETUP_STATE = UNKNOWN + 1;
	private static final int AUTHORIZATION_SETUP_STATE = SYSTEM_SETUP_STATE + 1;
	private static final int PIN_SETUP_STATE = AUTHORIZATION_SETUP_STATE + 1;
	private static final int LOCATION_SETUP_STATE = PIN_SETUP_STATE + 1;
	private static final int SERVER_NETWORK_SETUP_STATE = LOCATION_SETUP_STATE + 1; 
	private static final int CLIENT_NETWORK_SETUP_STATE = SERVER_NETWORK_SETUP_STATE + 1;
	private static final int REGISTRATION_SETUP_STATE = CLIENT_NETWORK_SETUP_STATE + 1;
	private static final int LOCATION_CONFIGURATION_SETUP_STATE = REGISTRATION_SETUP_STATE + 1;
	private static final int SERVER_OPERATIONS_SETUP = LOCATION_CONFIGURATION_SETUP_STATE + 1;
	private static final int CLIENT_OPERATIONS_SETUP = SERVER_OPERATIONS_SETUP + 1;
	private static final int READY_STATE = CLIENT_OPERATIONS_SETUP + 1;

	@Value("${application.name:Concessions Management System}")
	protected String applicationName;

	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;

	// Services
	@Autowired
	protected ApplicationConfigurationService appConfigService;

	@Autowired
	protected ServerConfigurationService serverConfigService;

	// Components
	@Autowired
	protected ApplicationFrame applicationFrame;
	
	// Beans
	@Autowired
	protected ApplicationModel appModel;

	@Autowired
	private ApplicationConfiguration appConfig;

	@Autowired
	private ServerConfiguration serverConfig;

	// Controllers
	@Autowired
	protected ClientOperationsState clientOperationsState;
	
	@Autowired
	protected DeviceCodeController deviceCodeController;

	@Autowired
	protected LocationSetupController locationSetupController;

	@Autowired
	protected PINController pinController;

	@Autowired
	@Qualifier("clientListener")
	protected LocalNetworkListener clientListener;
	
	@Autowired
	@Qualifier("serverListener")
	protected LocalNetworkListener serverListener;
	
	@Autowired
	protected LocationConfigurationState locationConfigurationState;

	@Autowired
	protected RegistrationState registrationState;
	
	@Autowired
	protected ServerOperationsState serverOperationsState;
	
	private int state = UNKNOWN;

	// HWC This is the old stuff, needs to be refactored
	@Autowired
	protected ServerApplicationModel applicationModel;

	@Autowired
	protected DeviceService deviceService;

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
	protected LocationConfigurationService locationConfigurationService;

	@Autowired
	protected OrderOrchestrator orderOrchestrator;

	@Autowired
	protected PreferenceService preferenceService;

	@Autowired
	protected LocationSetupController setupController;

	@Autowired
	protected TokenAuthService authService;

	public ServerApplication() {
	}

	@PostConstruct
	protected void initialize() {

		SwingUtilities.invokeLater(() -> {
			setupDesktopHandler(applicationFrame);
			WelcomePanel welcomePanel = new WelcomePanel(applicationName, applicationVersion);
			applicationFrame.addPanel(welcomePanel, WelcomePanel.NAME);
			applicationFrame.showPanel(WelcomePanel.NAME);
			applicationFrame.setVisible(true);
		});

		appConfig.addPropertyChangeListener(this);
		serverConfig.addPropertyChangeListener(this);

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
		 * 
		 * 
		 * journalNotifier.addJournalListener(new JournalListener() {
		 * 
		 * @Override public void journalStarted(JournalDTO journal) { }
		 * 
		 * @Override public void journalChanged (JournalDTO journal) { }
		 * 
		 * @Override public void journalOpened(JournalDTO journal) {
		 * journalCloseAction.setEnabled(true); journalSuspendAction.setEnabled(true);
		 * journalChange(journal); }
		 * 
		 * @Override public void journalClosed(JournalDTO journal) {
		 * journalChange(journal); }
		 * 
		 * @Override public void journalSuspended(JournalDTO journal) {
		 * journalChange(journal); }
		 * 
		 * public void journalSynced (JournalDTO journal) { }
		 * 
		 * protected void journalChange (JournalDTO journal) { List<Device> posDevices =
		 * deviceService.findByDeviceType(DeviceTypeType.POS); for (Device device :
		 * posDevices) { try { if (StringUtils.isNotBlank(device.getDeviceIp()) &&
		 * device.getDevicePort() > 0) { messenger.sendRequest(device.getDeviceIp(),
		 * device.getDevicePort(), NetworkConstants.JOURNAL_SERVICE,
		 * NetworkConstants.JOURNAL_CHANGE_ACTION, journal, SimpleResponseDTO.class); }
		 * } catch (MessengerException ex) { ex.printStackTrace(); } } } });
		 * 
		 * setupController.addSetupListener(new SetupController.SetupListener() {
		 * 
		 * @Override public void setupCompleted(LocationConfiguration
		 * organizationConfiguration) { executeSales(organizationConfiguration); } });
		 */
	}

	public synchronized void execute() {
		switch (state) {
		case SYSTEM_SETUP_STATE:
			showPanel(SystemSetupPanel.NAME);
			break;
		case AUTHORIZATION_SETUP_STATE:
			setupDeviceId();
			deviceCodeController.execute();
			break;
		case PIN_SETUP_STATE:
			pinController.execute();
			break;
		case LOCATION_SETUP_STATE:
			locationSetupController.execute();
			break;
		case SERVER_NETWORK_SETUP_STATE:
			appModel.setStatus("Starting server networking...");
			try {
				serverListener.start();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			updateState();
			break;
		case CLIENT_NETWORK_SETUP_STATE:
			appModel.setStatus("Starting client networking...");
			try {
				clientListener.start();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			updateState();
			break;
		case REGISTRATION_SETUP_STATE:
			appModel.setStatus("Registering client...");
			registrationState.execute();
			updateState();
			break;
		case LOCATION_CONFIGURATION_SETUP_STATE:
			appModel.setStatus("Retrieving location configuration...");
			locationConfigurationState.execute();
			updateState();
			break;
		case SERVER_OPERATIONS_SETUP:
			appModel.setStatus("Starting server...");
			serverOperationsState.execute();
			updateState();
			break;
		case CLIENT_OPERATIONS_SETUP:
			appModel.setStatus("Starting client...");
			clientOperationsState.execute();
			updateState();
			break;
		case READY_STATE:
			showPanel(WelcomePanel.NAME);
			appModel.setStatus("Ready");
			break;
		}
	}
	
	protected synchronized void updateState() {
		int oldState = this.state;
		int newState = SYSTEM_SETUP_STATE;

		if (appConfig.getApplicationRole() != ApplicationRole.UNDECIDED) {
			switch (appConfig.getApplicationRole()) {
			case SERVER:
				newState = AUTHORIZATION_SETUP_STATE;
				if (!deviceCodeController.isComplete()) {
					newState = AUTHORIZATION_SETUP_STATE;
				} else if (!pinController.isComplete()) {
					newState = PIN_SETUP_STATE;
				} else if (!locationSetupController.isComplete()) {
					newState = LOCATION_SETUP_STATE;
				} else if (!serverListener.isComplete()){
					newState = SERVER_NETWORK_SETUP_STATE;
				} else if (!clientListener.isComplete()) {
					newState = CLIENT_NETWORK_SETUP_STATE;
				} else if (!registrationState.isComplete()) {
					newState = REGISTRATION_SETUP_STATE;
				} else if (!locationConfigurationState.isComplete()) {
					newState = LOCATION_CONFIGURATION_SETUP_STATE;
				} else if (!serverOperationsState.isComplete()) {
					newState = SERVER_OPERATIONS_SETUP;
				} else if (!clientOperationsState.isComplete()) {
					newState = CLIENT_OPERATIONS_SETUP;
				} else {
					newState = READY_STATE;
				}
				break;
			case CLIENT:
				newState = READY_STATE;
				break;
			}

		}

		if (oldState != newState) {
			this.state = newState;
			execute();
		}
	}


	private void setupDeviceId() {
		// register our deviceId
		String deviceId = appConfig.getDeviceId();
		if (StringUtils.isBlank(deviceId)) {
			appModel.setStatus("Creating initial device...");
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
				appConfig.setDeviceId(deviceId);
				appConfig.setDeviceType(DeviceTypeType.SERVER);
				appConfigService.save();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * private void executeSetup(String organizationConfigurationIdText) { if
	 * (organizationConfigurationIdText == null) { organizationConfigurationIdText =
	 * preferenceService.get(LOCATION_CONFIGURATION_PREFERENCE); }
	 * 
	 * LocationConfiguration organizationConfiguration = null; if
	 * (organizationConfigurationIdText != null) { long organizationConfigurationId
	 * = Long.parseLong(organizationConfigurationIdText); try {
	 * organizationConfiguration =
	 * locationConfigurationService.get(organizationConfigurationId); if
	 * (organizationConfiguration != null) {
	 * applicationModel.setLocationConfiguration(organizationConfiguration);
	 * applicationModel.setOrganizationId(organizationConfiguration.
	 * getOrganizationId()); applicationModel.setStatusMessage("Ready");
	 * executeSales(organizationConfiguration); } } catch (Exception ex) {
	 * ex.printStackTrace(); } } if (organizationConfiguration == null) {
	 * setupController.execute(); } }
	 */

	/*
	 * private void executeSales(LocationConfiguration organizationConfiguration) {
	 * try { Menu menu = menuService.get(organizationConfiguration.getMenuId()); if
	 * (menu == null) { menu =
	 * menuRestClient.get(organizationConfiguration.getMenuId()).get();
	 * menuService.create(menu); } applicationModel.setMenu(MenuMapper.toDto(menu));
	 * journalController.initialize(); journalStartAction.setEnabled(true); } catch
	 * (Exception ex) { ex.printStackTrace(); } }
	 */

	protected boolean performQuit() {
		// Trigger the ExitAction when the user selects "Quit" from the macOS menu.
		exitAction.actionPerformed(new ActionEvent(applicationFrame, ActionEvent.ACTION_PERFORMED, "macOS_Quit_Menu"));

		return false;
	}

	protected void showAboutDialog(JFrame ownerFrame) {
		AboutDialog.showAboutDialog(ownerFrame, applicationName, applicationVersion);
	}

	protected void showPanel(String name) {
		SwingUtilities.invokeLater(() -> {
			applicationFrame.showPanel(name);
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() != appConfig && !evt.getPropertyName().equals(ApplicationConfiguration.PROPERTY_JOURNAL) ) {
			updateState();
		}

		/*
		 * if (ServerApplicationModel.CONNECTED.equals(evt.getPropertyName()) ||
		 * ServerApplicationModel.TOKEN_RESPONSE.equals(evt.getPropertyName())) {
		 * setupAction.setEnabled(applicationModel.isConnected() &&
		 * serverConfig.getTokenResponse() != null);
		 * loginAction.setEnabled(applicationModel.isConnected() &&
		 * serverConfig.getTokenResponse() == null);
		 * logoutAction.setEnabled(serverConfig.getTokenResponse() != null); } else if
		 * (ServerApplicationModel.JOURNAL.equals(evt.getPropertyName())) {
		 * orderOrchestrator.initialize((JournalDTO)evt.getNewValue()); }
		 */
	}

	public static void main(String[] args) {
		initializeLaF("CMS Server");

		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			ConfigurableEnvironment environment = context.getEnvironment();

			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
			ClassPathResource resource = new ClassPathResource("application-server.yml");
			PropertySource<?> yamlPropertySource = loader.load("application-server.yml", resource).get(0); // Take the
																											// first
																											// (and
																											// usually
																											// only)
																											// document
			environment.getPropertySources().addLast(yamlPropertySource);

			context.register(JpaConfig.class);
			context.register(AppConfig.class);
			// context.scan("com.concessions.local", "com.concessions.client",
			// "com.concessions.common");
			context.refresh();
			context.registerShutdownHook();

			ServerApplication application = context.getBean(ServerApplication.class);
			application.updateState();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start Server application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			if (context != null) {
				context.close();
			}
			System.exit(1);
		}
	}
}
