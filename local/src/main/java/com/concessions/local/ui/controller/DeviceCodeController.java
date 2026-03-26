package com.concessions.local.ui.controller;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.prefs.BackingStoreException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.network.NetworkUtil;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.QRGeneratorService;
import com.concessions.local.service.ServerConfigurationService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.DeviceCodePanel;

import jakarta.annotation.PostConstruct;

@Component
public class DeviceCodeController {

	private static final Logger logger = LoggerFactory.getLogger(DeviceCodeController.class);

	@Autowired
	protected ApplicationConfiguration appConfig;
	
	@Autowired
	protected ApplicationConfigurationService appConfigService;
	
	@Autowired
	protected ApplicationFrame applicationFrame;

	@Autowired
	protected ApplicationModel appModel;

	@Autowired
	protected QRGeneratorService qrService;
	
	@Autowired
	protected ServerConfiguration serverConfig;
	
	@Autowired
	protected ServerConfigurationService serverConfigService;

	@Autowired
	protected TokenAuthService authService;

	protected DeviceCodePanel deviceCodePanel;

	public DeviceCodeController() {
	}

	public void execute() {
		if (NetworkUtil.isConnected()) {
			TokenResponse tokenResponse = serverConfig.getTokenResponse();
			if (tokenResponse == null) {
				appModel.setStatus("Starting authentication...");
				initiateLoginFlow();
			} else {
				logger.debug("refresh token " + tokenResponse.refresh_token());
				if (!authService.isTokenValid(tokenResponse)) {
					logger.debug("refreshing token...");
					authService.refreshToken(tokenResponse.refresh_token()).thenAccept(newToken -> {
						serverConfig.setTokenResponse(newToken);
						try {
							serverConfigService.save();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}).exceptionally(ex -> {
						// Refresh failed (e.g., refresh token expired). Force new device login.
						initiateLoginFlow();
						return null;
					});
								}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Failed to start authentication: No network connection", "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public boolean isComplete () {
		return serverConfig.getTokenResponse() != null;
	}
	
	@PostConstruct
	protected void initialize() {
		this.deviceCodePanel = new DeviceCodePanel(qrService, new DeviceCodePanel.DeviceCodeUIListener() {
		    @Override
		    public void onCancel() {
		    	try {
		    		appConfigService.reset();
		    	} catch (BackingStoreException ex) {
		    		ex.printStackTrace();
		    	}
		    	
		        authService.cancelPolling(); 
		    }
		});
		
		this.applicationFrame.addPanel(deviceCodePanel, DeviceCodePanel.NAME);
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
				SwingUtilities.invokeLater(() -> {
					deviceCodePanel.setResponse(response);
					applicationFrame.showPanel(DeviceCodePanel.NAME);
				});

				// 3. Poll for Token (Blocks the worker thread until authorization completes)
				tokenResponse = authService.pollForToken(response).join();
				return null;
			}

			@Override
			protected void done() {
				// This method runs on the EDT, safe for UI updates
				try {
					get(); // This retrieves the result or re-throws exceptions from doInBackground()
					appModel.setStatus("Authenticated.");
					logger.debug("Access Token received: " + tokenResponse.access_token());
					
					JOptionPane.showMessageDialog(applicationFrame, "Login Successful!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					serverConfig.setTokenResponse(tokenResponse);
					serverConfigService.save();

				} catch (Exception ex) {
					// Handle join() exceptions and nested exceptions
					Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
					Throwable rootCause = cause;
					while (rootCause.getCause() != null) {
						rootCause = rootCause.getCause();
					}
					
					appModel.setStatus("Authentication Failed.");
					if (!(rootCause instanceof CancellationException)) {
						rootCause.printStackTrace();
						JOptionPane.showMessageDialog(applicationFrame, "Authentication Failed: " + rootCause.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		worker.execute();
	}

	public interface DeviceCodeListener {
		void onDeviceCodeAuthenticated(TokenResponse tokenResponse);
		
		void onDeviceCodeFailed ();
	}
}
