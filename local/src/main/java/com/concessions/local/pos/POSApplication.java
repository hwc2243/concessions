package com.concessions.local.pos;

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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;

import com.concessions.common.network.RegistrationClient;
import com.concessions.common.network.dto.WelcomeResponseDTO;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.base.AbstractApplication;
import com.concessions.local.base.ui.AboutDialog;
import com.concessions.local.base.ui.PINController;
import com.concessions.local.model.DeviceTypeType;
import com.concessions.local.network.client.ClientService;
import com.concessions.local.network.dto.DeviceRegistrationRequestDTO;
import com.concessions.local.pos.config.AppConfig;
import com.concessions.local.pos.ui.POSApplicationFrame;

@Component
public class POSApplication extends AbstractApplication {

	private static final Logger logger = LoggerFactory.getLogger(POSApplication.class);
	
	@Value("${application.name:Concessions Management System POS}")
	protected String applicationName;
	
	@Value("${application.version:SNAPSHOT}")
	protected String applicationVersion;

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private PINController pinController;
	
	@Autowired
	private POSApplicationFrame frame;
	
	@Autowired
	private PreferenceService preferenceService;
	
	@Autowired
	protected RegistrationClient registrationClient;

	public POSApplication() {
		// TODO Auto-generated constructor stub
	}

	public void execute ()
	{
		logger.info("Starting POS Application");
		String deviceId = preferenceService.get(DEVICE_ID_PREFERENCE);
		if (StringUtils.isBlank(deviceId)) {
			deviceId = UUID.randomUUID().toString();
			try {
				preferenceService.save(DEVICE_ID_PREFERENCE, deviceId);
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
		
		WelcomeResponseDTO welcomeResponse = registrationClient.discoverService();
		if (welcomeResponse == null) {
			logger.error("Failed to locate server.");
			System.exit(1);
		}
		clientService.initialize(welcomeResponse.getServerIp(), welcomeResponse.getServerPort());
		
		// Show the main application window
		SwingUtilities.invokeLater(() -> {
			setupDesktopHandler(frame);

			frame.setVisible(true);
		});
		
		String pin = preferenceService.get(PIN_PREFERENCE);
		pinController.execute(frame, pin);
		
		DeviceRegistrationRequestDTO deviceRegistration = new DeviceRegistrationRequestDTO();
		deviceRegistration.setDeviceId(deviceId);
		deviceRegistration.setDeviceType(DeviceTypeType.POS);
	}
	
	public static void main(String[] args) {
		initializeLaF("CMS POS");
	
		AnnotationConfigApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext();
			ConfigurableEnvironment environment = context.getEnvironment();

	        // Load application.yml from classpath
	        environment.getPropertySources().addLast(
	                new ResourcePropertySource("application-pos.yml", "classpath:application-pos.yml"));
			
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
