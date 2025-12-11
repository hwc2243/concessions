package com.concessions.local.base.ui;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

@Component
public class PINDialog extends JDialog {

	
	private JPasswordField pinField;
	private JPasswordField confirmField;
	private JButton okButton;
	private JButton cancelButton;
	
	public PINDialog () {
		super(null, "Set PIN", Dialog.ModalityType.APPLICATION_MODAL);
		
		initializeDialog();
	}

	private JPanel createPinGroup(String labelText, JPasswordField passwordField) {
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        passwordField.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setPreferredSize(new Dimension(250, 30));
        
        groupPanel.add(label);
        groupPanel.add(Box.createVerticalStrut(5));
        groupPanel.add(passwordField);
        
        return groupPanel;
    }
	
	private void initializeDialog() {
		setLayout(new BorderLayout(10, 10));
        setSize(400, 300);
        //setLocationRelativeTo(application); // Corrected field name
        setResizable(false);
        
        // Panel to hold both Org selection and Location selection (stacked vertically)
        JPanel selectionContainer = new JPanel();
        selectionContainer.setLayout(new BoxLayout(selectionContainer, BoxLayout.Y_AXIS));
        selectionContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        // --- PIN fields
        pinField = new JPasswordField(10); // 10 columns wide, but max size controls width
        pinField.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel pinPanel = createPinGroup("Enter Device PIN (4-6 digits):", pinField);
        selectionContainer.add(pinPanel);
        selectionContainer.add(Box.createVerticalStrut(15));
        
        // Confirm PIN Field
        confirmField = new JPasswordField(10);
        confirmField.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel confirmPinPanel = createPinGroup("Confirm Device PIN:", confirmField);
        selectionContainer.add(confirmPinPanel);
        selectionContainer.add(Box.createVerticalStrut(20));
        
        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("Set");
        okButton.setActionCommand(OK_COMMAND);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(CANCEL_COMMAND);
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(selectionContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
	
	public void addActionListener (ActionListener listener) {
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
	}
	
	/**
     * Retrieves the text entered in the PIN field as a String.
     * NOTE: char[] is preferred for security, but often String is used for simple retrieval.
     */
    public String getPin() {
        return new String(pinField.getPassword());
    }

    /**
     * Retrieves the text entered in the Confirm PIN field as a String.
     */
    public String getConfirmPin() {
        return new String(confirmField.getPassword());
    }
}
