package com.concessions.local.ui.controller;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.prefs.BackingStoreException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.QRGeneratorService;
import com.concessions.local.service.ServerConfigurationService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.view.DeviceCodePanel;
import com.concessions.local.util.NetworkUtil;

import jakarta.annotation.PostConstruct;

@Component
public class DeviceCodeController {

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

	private List<DeviceCodeListener> listeners = new java.util.ArrayList<>();

	public DeviceCodeController() {
	}

	public void execute() {
		if (NetworkUtil.isConnected()) {
			TokenResponse tokenResponse = serverConfig.getTokenResponse();
			if (tokenResponse == null) {
				appModel.setStatus("Starting authentication...");
				initiateLoginFlow();
			} else {
				System.out.println("refresh token " + tokenResponse.refresh_token());
				if (!authService.isTokenValid(tokenResponse)) {
					System.out.println("refreshing token...");
					authService.refreshToken(tokenResponse.refresh_token()).thenAccept(newToken -> {
						serverConfig.setTokenResponse(newToken);
						try {
							serverConfigService.save();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						notifyAuthenticated(newToken);
					}).exceptionally(ex -> {
						// Refresh failed (e.g., refresh token expired). Force new device login.
						initiateLoginFlow();
						return null;
					});
				} else {
					notifyAuthenticated(tokenResponse);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Failed to start authentication: No network connection", "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			notifyFailed();
		}
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

	public void addDeviceCodeListener(DeviceCodeListener listener) {
		listeners.add(listener);
	}

	public void removeDeviceCodeListener(DeviceCodeListener listener) {
		listeners.remove(listener);
	}

	protected void notifyAuthenticated (TokenResponse tokenResponse) {
		listeners.stream().forEach(listener -> listener.onDeviceCodeAuthenticated(tokenResponse));
	}

	protected void notifyFailed () {
		listeners.stream().forEach(listener -> listener.onDeviceCodeFailed());
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
					showDeviceCodePanel(response);
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
					System.out.println("Access Token received: " + tokenResponse.access_token());
					/* Close the modal if it's still open
					if (deviceCodeModal.isVisible()) {
						deviceCodeModal.setVisible(false);
					}
					*/
					JOptionPane.showMessageDialog(applicationFrame, "Login Successful!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					serverConfig.setTokenResponse(tokenResponse);
					serverConfigService.save();

					notifyAuthenticated(tokenResponse);

				} catch (Exception ex) {
					// Handle join() exceptions and nested exceptions
					Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
					Throwable rootCause = cause;
					while (cause.getCause() != null) {
						rootCause = rootCause.getCause();
					}
					
					appModel.setStatus("Authentication Failed.");
					if (!(rootCause instanceof CancellationException)) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(applicationFrame, "Authentication Failed: " + cause.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					}

					notifyFailed();
				}
			}
		};
		worker.execute();
	}

	/**
	 * Creates and displays a panel with the Keycloak user code and
	 * verification URI.
	 */
	private void showDeviceCodePanel (TokenAuthService.DeviceCodeResponse response) {
		SwingUtilities.invokeLater(() -> {
			deviceCodePanel.setResponse(response);
			applicationFrame.showPanel(DeviceCodePanel.NAME);
		});
	}

	public interface DeviceCodeListener {
		void onDeviceCodeAuthenticated(TokenResponse tokenResponse);
		
		void onDeviceCodeFailed ();
	}
}
