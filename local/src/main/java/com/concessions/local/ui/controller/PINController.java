package com.concessions.local.ui.controller;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.PINVerifyRequestDTO;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.common.service.PreferenceService;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ServerConfiguration;
import com.concessions.local.network.Messenger;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.service.ApplicationConfigurationService;
import com.concessions.local.service.ServerConfigurationService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.PINPanel;
import com.nimbusds.oauth2.sdk.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Component
public class PINController {

	private static final Logger logger = LoggerFactory.getLogger(PINController.class);

	@Autowired
	protected ApplicationFrame applicationFrame;

	@Autowired
	protected ApplicationModel appModel;

	@Autowired
	protected ApplicationConfiguration appConfig;
	
	@Autowired
	protected ApplicationConfigurationService appConfigService;

	@Autowired
	protected Messenger messenger;
	
	@Autowired
	protected ServerConfigurationService serverConfigService;

	private List<PINListener> listeners = new java.util.ArrayList<>();

	private PINPanel pinPanel;

	private String pin;

	public PINController() {
	}

	@PostConstruct
	private void initializeController() {
		pinPanel = new PINPanel();
		pinPanel.setupNavigationListeners(pin -> {
			// Logic for 'Next'
			System.out.println("Saving PIN: " + pin);
			appConfig.setPin(pin);
			try {
				appConfigService.save();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}, () -> {
			try {
				appConfigService.reset();
				serverConfigService.reset();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		});
		applicationFrame.addPanel(pinPanel, PINPanel.NAME);
	}

	public boolean isComplete() {
		return StringUtils.isNotBlank(appConfig.getPin());
	}

	public void execute() {

		SwingUtilities.invokeLater(() -> {
			appModel.setStatus("Configuring security...");
			applicationFrame.showPanel(PINPanel.NAME);
		});
	}

	public String getPIN() {
		return this.pin;
	}

	protected boolean isValidPin(String pin) {
		if (pin == null || pin.isEmpty()) {
			return false;
		}

		// Check if the pin contains only digits
		for (char c : pin.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	protected boolean verifyPIN(String pin) {
		PINVerifyRequestDTO pinVerify = new PINVerifyRequestDTO();
		pinVerify.setPIN(pin);
		try {
			messenger.sendRequest(NetworkConstants.PIN_SERVICE, NetworkConstants.PIN_VERIFY_ACTION, pinVerify,
					SimpleResponseDTO.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	public void addPINListener(PINListener listener) {
		listeners.add(listener);
	}

	public void removePINListener(PINListener listener) {
		listeners.remove(listener);
	}

	protected void notifyPINSet(String pin) {
		for (PINListener listener : listeners) {
			listener.pinSet(pin);
		}
	}

	public interface PINListener {
		public void pinSet(String pin);
	}
}
