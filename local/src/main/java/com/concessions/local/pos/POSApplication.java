package com.concessions.local.pos;

import static com.concessions.local.base.Constants.DEVICE_ID_PREFERENCE;
import static com.concessions.local.base.Constants.PIN_PREFERENCE;

import java.beans.PropertyChangeListener;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;

import com.concessions.common.event.JournalNotifier;
import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.RegistrationClient;
import com.concessions.common.network.dto.ConfigurationResponseDTO;
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO;
import com.concessions.common.network.dto.DeviceRegistrationResponseDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.common.network.dto.WelcomeResponseDTO;
import com.concessions.common.service.PreferenceService;
import com.concessions.dto.JournalDTO;
import com.concessions.dto.MenuDTO;
import com.concessions.local.base.AbstractApplication;
import com.concessions.local.base.AbstractClientApplication;
import com.concessions.local.base.ui.AboutDialog;
import com.concessions.local.base.ui.PINController;
import com.concessions.local.model.DeviceTypeType;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.network.server.ConfigurationHandler;
import com.concessions.local.network.server.DeviceHandler;
import com.concessions.local.network.server.JournalHandler;
import com.concessions.local.network.server.MenuHandler;
import com.concessions.local.network.server.OrderHandler;
import com.concessions.local.pos.config.AppConfig;
import com.concessions.local.pos.controller.OrderSubmissionController;
import com.concessions.local.pos.model.POSApplicationModel;
import com.concessions.local.pos.ui.POSApplicationFrame;
import com.concessions.local.ui.controller.OrderController;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.view.OrderPanel;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(
	    prefix = "local.network",
	    name = "client",
	    havingValue = "true",
	    matchIfMissing = false // This ensures if the property is not defined, the component is NOT created.
	)
public class POSApplication extends AbstractClientApplication {

	private static final Logger logger = LoggerFactory.getLogger(POSApplication.class);
	
	@Value("${application.name:Concessions Management System POS}")
	protected String applicationName;
	
	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;

	@Autowired
	private POSApplicationFrame frame;
	
	@Autowired
	private POSApplicationModel model;
	
	@Autowired
	protected JournalNotifier journalNotifier;

	public POSApplication() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	protected void initialize () {
		logger.info("Starting POS Application");
		pinController.addPINListener(new PINController.PINListener () {
			public void pinSet (String pin) {
				model.setPin(pin);
				executeStartup();
			}
		});
	}
	
	public void execute ()
	{
		String deviceId = preferenceService.get(DEVICE_ID_PREFERENCE);
		if (StringUtils.isBlank(deviceId)) {
			deviceId = UUID.randomUUID().toString();
			try {
				preferenceService.save(DEVICE_ID_PREFERENCE, deviceId);
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
		model.setDeviceId(deviceId);
		
		WelcomeResponseDTO welcomeResponse = registrationClient.discoverService();
		if (welcomeResponse == null) {
			logger.error("Failed to locate server.");
			System.exit(1);
		}
		messenger.initialize(welcomeResponse.getServerIp(), welcomeResponse.getServerPort());
		
		// Show the main application window
		SwingUtilities.invokeLater(() -> {
			setupDesktopHandler(frame);

			frame.setVisible(true);
		});
		
		String pin = preferenceService.get(PIN_PREFERENCE);
		pinController.execute(frame, pin);
	}

	protected void executeStartup () {
		model.setStatusMessage("Configuring");
		executeDeviceRegistration();
		executeLocationConfiguration();
		executeMenu();
		executeStartOrders();
		model.setStatusMessage("Ready");
	}
	
	protected void executeDeviceRegistration () {
		DeviceRegistrationRequestDTO deviceRegistration = new DeviceRegistrationRequestDTO();
		deviceRegistration.setDeviceId(model.getDeviceId());
		deviceRegistration.setDeviceType(DeviceTypeType.POS.name());
		deviceRegistration.setDeviceIp(localNetworkListener.getListenerIp());
		deviceRegistration.setDevicePort(localNetworkListener.getListenerPort());
		deviceRegistration.setPIN(model.getPin());
		
		DeviceRegistrationResponseDTO deviceRegistrationResponse;
		try {
			deviceRegistrationResponse = messenger.sendRequest(NetworkConstants.DEVICE_SERVICE, NetworkConstants.DEVICE_REGISTER_ACTION, deviceRegistration, DeviceRegistrationResponseDTO.class);
			model.setDeviceNumber(deviceRegistrationResponse.getDeviceNumber());
		} catch (MessengerException ex) {
			JOptionPane.showMessageDialog(null, "Failed to register device - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
	}

	protected void executeLocationConfiguration () {
		SimpleDeviceRequestDTO request = new SimpleDeviceRequestDTO();
		request.setPIN(model.getPin());
		request.setDeviceId(model.getDeviceId());
		
		ConfigurationResponseDTO response = null;
		try {
			response = messenger.sendRequest(NetworkConstants.CONFIGURATION_SERVICE, NetworkConstants.CONFIGURATION_LOCATION_ACTION, request, ConfigurationResponseDTO.class);
			LocationConfiguration locationConfiguration = new LocationConfiguration();
			locationConfiguration.setOrganizationName(response.getOrganizationName());
			locationConfiguration.setLocationName(response.getLocationName());
			locationConfiguration.setMenuName(response.getMenuName());
			model.setLocationConfiguration(locationConfiguration);
		} catch (MessengerException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve location configuration - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	protected void executeMenu () {
		SimpleDeviceRequestDTO request = new SimpleDeviceRequestDTO();
		request.setPIN(model.getPin());
		request.setDeviceId(model.getDeviceId());
		
		MenuDTO response = null;
		try
		{
			response = messenger.sendRequest(NetworkConstants.MENU_SERVICE, NetworkConstants.MENU_GET_ACTION, request, MenuDTO.class);
			model.setMenu(response);
		} catch (MessengerException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve location configuration - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	protected void executeStartOrders () {
		SimpleDeviceRequestDTO request = new SimpleDeviceRequestDTO();
		request.setPIN(model.getPin());
		request.setDeviceId(model.getDeviceId());
		
		JournalDTO journal = null;
		try {
			journal = messenger.sendRequest(NetworkConstants.JOURNAL_SERVICE, NetworkConstants.JOURNAL_GET_ACTION, request, JournalDTO.class);
			model.setJournal(journal);
		} catch (MessengerException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve journal - " + ex.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(1);
		}

		OrderSubmissionController orderSubmissionController = new OrderSubmissionController(model, messenger);
		OrderController controller = new OrderController(frame);
		journalNotifier.addJournalListener(controller);
		controller.addOrderListener(orderSubmissionController);
		controller.execute(model.getMenu(), journal);
	}
	
	public static void main(String[] args) {
		initializeLaF("CMS POS");
	
		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			ConfigurableEnvironment environment = context.getEnvironment();

			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
			ClassPathResource resource = new ClassPathResource("application-pos.yml");
			PropertySource<?> yamlPropertySource = loader.load("application-pos.yml", resource)
			                                            .get(0); // Take the first (and usually only) document
	        environment.getPropertySources().addLast(yamlPropertySource);
			
			context.register(AppConfig.class);
            context.scan("com.concessions.local.pos", "com.concessions.local.base", "com.concessions.local.network.client", "com.concessions.common");
            context.refresh();
            context.registerShutdownHook();

			POSApplication application = context.getBean(POSApplication.class);	
			application.execute();


		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start POS application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			if (context != null)
			{
				context.close();
			}
			System.exit(1);
		}
	}

	@Override
	protected void showAboutDialog (JFrame frame) {
        AboutDialog.showAboutDialog(frame, applicationName, applicationVersion);
	}

	@Override
	protected boolean performQuit() {
		return true;
	}
}
