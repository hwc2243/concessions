package com.concessions.local.ui.controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.security.TokenAuthService;
import com.concessions.local.security.TokenAuthService.TokenResponse;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.model.ApplicationModel;
import com.concessions.local.ui.view.DeviceCodeDialog;
import com.concessions.local.util.NetworkUtil;

import jakarta.annotation.PostConstruct;

@Component
public class DeviceCodeController {

	@Autowired
	protected LoginAction loginAction;

	@Autowired
	protected LogoutAction logoutAction;

	@Autowired
	protected ApplicationFrame applicationFrame;

	@Autowired
	protected ApplicationModel applicationModel;

	@Autowired
	protected DeviceCodeDialog deviceCodeModal;

	@Autowired
	protected TokenAuthService authService;

	private List<DeviceCodeListener> listeners = new java.util.ArrayList<>();

	public DeviceCodeController() {
	}

	public void execute() {
		if (NetworkUtil.isConnected()) {
			TokenResponse tokenResponse = applicationModel.getTokenResponse();
			if (tokenResponse == null) {
				applicationModel.setStatusMessage("Starting authentication...");
				loginAction.setEnabled(true);
				logoutAction.setEnabled(false);
				initiateLoginFlow();
			} else {
				System.out.println("refresh token " + tokenResponse.refresh_token());
				System.out.println("Using stored access token, expires in " + tokenResponse.expires_in() + " seconds.");
				if (!authService.isTokenValid(tokenResponse)) {
					System.out.println("refreshing token...");
					authService.refreshToken(tokenResponse.refresh_token()).thenAccept(newToken -> {
						applicationModel.setTokenResponse(newToken);
						authService.storeTokenResponse(newToken);
						loginAction.setEnabled(false);
						logoutAction.setEnabled(true);
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
		applicationModel.addPropertyChangeListener(evt -> {
			if ("tokenResponse".equals(evt.getPropertyName())) {
				TokenResponse tokenResponse = (TokenResponse) evt.getNewValue();
				boolean isAuthenticated = tokenResponse != null;
				loginAction.setEnabled(!isAuthenticated);
				logoutAction.setEnabled(isAuthenticated);
			}
		});
	}

	public void addDeviceCodeListener(DeviceCodeListener listener) {
		listeners.add(listener);
	}

	public void removeDeviceCodeListener(DeviceCodeListener listener) {
		listeners.remove(listener);
	}

	protected void notifyAuthenticated (TokenResponse tokenResponse) {
		for (DeviceCodeListener listener : listeners) {
			listener.onDeviceCodeAuthenticated(tokenResponse);
		}
	}

	protected void notifyFailed () {
		for (DeviceCodeListener listener : listeners) {
			listener.onDeviceCodeFailed();
		}
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
					showDeviceCodeModal(response);
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
					applicationModel.setStatusMessage("Authenticated! Token acquired.");
					System.out.println("Access Token received: " + tokenResponse.access_token());
					// Close the modal if it's still open
					if (deviceCodeModal.isVisible()) {
						deviceCodeModal.setVisible(false);
					}
					JOptionPane.showMessageDialog(applicationFrame, "Login Successful!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					applicationModel.setTokenResponse(tokenResponse);
					authService.storeTokenResponse(tokenResponse);

					notifyAuthenticated(tokenResponse);

				} catch (Exception ex) {
					// Handle join() exceptions and nested exceptions
					Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
					ex.printStackTrace();
					applicationModel.setStatusMessage("Authentication Failed.");
					JOptionPane.showMessageDialog(applicationFrame, "Authentication Failed: " + cause.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);

					// Close the modal if it's still open
					if (deviceCodeModal.isVisible()) {
						deviceCodeModal.setVisible(false);
					}
					notifyFailed();
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
		deviceCodeModal.showDialog(response.verification_uri(), response.user_code(), response.expires_in());
	}

	public interface DeviceCodeListener {
		void onDeviceCodeAuthenticated(TokenResponse tokenResponse);
		
		void onDeviceCodeFailed ();
	}
}
