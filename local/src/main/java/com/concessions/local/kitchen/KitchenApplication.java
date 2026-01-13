package com.concessions.local.kitchen;

import static com.concessions.local.base.Constants.DEVICE_ID_PREFERENCE;
import static com.concessions.local.base.Constants.PIN_PREFERENCE;

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
import org.springframework.stereotype.Component;

import com.concessions.common.network.MessengerException;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.RegistrationClient;
import com.concessions.common.network.dto.ConfigurationResponseDTO;
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO;
import com.concessions.common.network.dto.DeviceRegistrationResponseDTO;
import com.concessions.common.network.dto.SimpleDeviceRequestDTO;
import com.concessions.common.network.dto.WelcomeResponseDTO;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.base.AbstractApplication;
import com.concessions.local.base.ui.AboutDialog;
import com.concessions.local.base.ui.PINController;
import com.concessions.local.kitchen.config.AppConfig;
import com.concessions.local.kitchen.model.KitchenApplicationModel;
import com.concessions.local.kitchen.ui.KitchenApplicationFrame;
import com.concessions.local.model.DeviceTypeType;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.network.server.ConfigurationHandler;
import com.concessions.local.network.server.DeviceHandler;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(
	    prefix = "local.network",
	    name = "client",
	    havingValue = "true",
	    matchIfMissing = false // This ensures if the property is not defined, the component is NOT created.
	)
public class KitchenApplication extends AbstractApplication {

	private static final Logger logger = LoggerFactory.getLogger(KitchenApplication.class);

	@Value("${application.name:Concessions Management System Kitchen}")
	protected String applicationName;
	
	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;

	@Autowired
	private PINController pinController;
	
	@Autowired
	private KitchenApplicationFrame frame;
	
	@Autowired
	private KitchenApplicationModel model;
	
	@Autowired
	private PreferenceService preferenceService;
	
	@Autowired
	protected RegistrationClient registrationClient;
	
	public KitchenApplication() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	protected void initialize () {
		logger.info("Starting Kitchen Application");
		pinController.addPINListener(new PINController.PINListener () {
			public void pinSet (String pin) {
				model.setPin(pin);
				executeStartup();
			}
		});
	}
	
	protected void execute () {
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
	
	public void executeStartup () {
		model.setStatusMessage("Configuring");
		executeDeviceRegistration();
		executeLocationConfiguration();
		model.setStatusMessage("Ready");
	}
	
	protected void executeDeviceRegistration () {
		DeviceRegistrationRequestDTO deviceRegistration = new DeviceRegistrationRequestDTO();
		deviceRegistration.setDeviceId(model.getDeviceId());
		deviceRegistration.setDeviceType(DeviceTypeType.KITCHEN.name());
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
	@Override
	protected void showAboutDialog(JFrame frame) {
		AboutDialog.showAboutDialog(frame, applicationName, applicationVersion);
	}

	@Override
	protected boolean performQuit() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		initializeLaF("CMS Kitchen");
	
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
            context.scan("com.concessions.local.kitchen", "com.concessions.local.base", "com.concessions.local.network.client", "com.concessions.common");
            context.refresh();
            context.registerShutdownHook();

			KitchenApplication application = context.getBean(KitchenApplication.class);	
			application.execute();


		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start Kitchen application: " + e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			if (context != null)
			{
				context.close();
			}
			System.exit(1);
		}
	}
}
