package com.concessions.local.base.ui;

import static com.concessions.local.base.Constants.PIN_PREFERENCE;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.common.service.PreferenceService;
import com.concessions.local.network.client.ClientService;
import com.concessions.local.network.dto.PINVerifyRequest;
import com.concessions.local.network.dto.SimpleResponseDTO;
import com.concessions.local.network.manager.PINManager;
import com.nimbusds.oauth2.sdk.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Component
public class PINController {

	private static final Logger logger = LoggerFactory.getLogger(PINController.class);
	
	@Autowired
	protected ClientService clientService;
	
	@Autowired
	protected PreferenceService preferenceService;

	private List<PINListener> listeners = new java.util.ArrayList<>();

	private PINDialog view;
	
	public PINController() {
	}

	@PostConstruct
	private void initializeController() {
		view = new PINDialog();
		view.addActionListener(e -> {
			switch (e.getActionCommand()) {
			case OK_COMMAND:
				
				String pin = view.getPin();
				String confirmPin = view.getConfirmPin();
				
				// 1. Validate PIN is purely numeric and not empty
				if (!isValidPin(pin)) {
					JOptionPane.showMessageDialog(view, "The Device PIN must be purely numeric and cannot be empty.", 
							"Validation Error", JOptionPane.ERROR_MESSAGE);
					return; // Stop processing and keep dialog open
				}
				
				// 2. Validate PIN and Confirm PIN match
				if (!pin.equals(confirmPin)) {
					JOptionPane.showMessageDialog(view, "The Device PIN and Confirm PIN do not match.", 
							"Validation Error", JOptionPane.ERROR_MESSAGE);
					return; // Stop processing and keep dialog open
				}
				
				if (verifyPIN(pin)) {
				
					try {
						preferenceService.save(PIN_PREFERENCE, pin);
						notifyPINSet(pin);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					return;
				}
				break;
			case CANCEL_COMMAND:
				break;
			}
			
			view.setVisible(false);
		});
	}

	private boolean isValidPin(String pin) {
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
	
	public void execute (JFrame frame, String pin) {
		if (StringUtils.isBlank(pin) || !verifyPIN(pin)) {
			SwingUtilities.invokeLater(() -> {
				view.setLocationRelativeTo(frame);
				view.pack();
				view.setVisible(true);
			});
		}
	}

	protected boolean verifyPIN (String pin) {
		PINVerifyRequest pinVerify = new PINVerifyRequest();
		pinVerify.setPIN(pin);
		try {
			clientService.sendRequest(PINManager.NAME, PINManager.VERIFY_ACTION, pinVerify, SimpleResponseDTO.class);
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

	protected void notifyPINSet (String pin) {
		for (PINListener listener : listeners) {
			listener.pinSet(pin);
		}
	}
	
	public interface PINListener {
		void pinSet (String pin);
	}
}
