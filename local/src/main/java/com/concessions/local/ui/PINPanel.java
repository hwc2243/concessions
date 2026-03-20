package com.concessions.local.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PINPanel extends JPanel {

    public static final String NAME = "PIN";
    
    private JPasswordField pinField;
    private JPasswordField confirmField;
    private JButton nextButton;
    private JButton cancelButton;
    
    public PINPanel() {
        initialize();
    }

    /**
     * Initializes the UI with a centered, modern look.
     * @param onNext Lambda called when 'Next' is clicked, passing the entered PIN.
     * @param onCancel Lambda called when 'Cancel' is clicked.
     */
    public void setupNavigationListeners(Consumer<String> onNext, Runnable onCancel) {
        nextButton.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            if (pin.isEmpty() || !pin.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "PINs do not match or are empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } else {
                onNext.accept(pin);
            }
        });
        
        cancelButton.addActionListener(e -> onCancel.run());
    }

    private void initialize() {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout()); // Ensures content stays centered in the panel

        // Main container for content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- Header Section ---
        JLabel titleLabel = new JLabel("Security Setup");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JLabel subTitleLabel = new JLabel("Create a PIN to secure this device.");
        subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateInputs(); }
            public void removeUpdate(DocumentEvent e) { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        
        // --- PIN Fields ---
        pinField = createStyledPasswordField();
        pinField.getDocument().addDocumentListener(listener);
        JPanel pinGroup = createPinGroup("Enter Device PIN (4-6 digits)", pinField);

        confirmField = createStyledPasswordField();
        confirmField.getDocument().addDocumentListener(listener);
        JPanel confirmGroup = createPinGroup("Confirm Device PIN", confirmField);

        // --- Action Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.setPreferredSize(new Dimension(100, 35));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        // --- Add components to content box ---
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(subTitleLabel);
        content.add(Box.createVerticalStrut(30));
        content.add(pinGroup);
        content.add(Box.createVerticalStrut(20));
        content.add(confirmGroup);
        content.add(Box.createVerticalStrut(30));
        content.add(buttonPanel);

        // Add the content box to the centered GridBagLayout
        add(content, new GridBagConstraints());
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField(10);
        pf.setFont(new Font("SansSerif", Font.BOLD, 20));
        pf.setHorizontalAlignment(SwingConstants.CENTER);
        pf.setMaximumSize(new Dimension(200, 40));
        pf.setPreferredSize(new Dimension(200, 40));
        pf.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        return pf;
    }

    private JPanel createPinGroup(String labelText, JPasswordField passwordField) {
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        groupPanel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.DARK_GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a sub-panel for the field + toggle button
        JPanel inputWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        inputWrapper.setBackground(Color.WHITE);

        // The Toggle Button
        JButton toggleButton = new JButton("👁");
        toggleButton.setPreferredSize(new Dimension(45, 40));
        toggleButton.setFocusPainted(false);
        toggleButton.setToolTipText("Show/Hide PIN");

        // Capture the original echo character (usually '•')
        char defaultEchoChar = passwordField.getEchoChar();

        toggleButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == (char) 0) {
                passwordField.setEchoChar(defaultEchoChar);
                toggleButton.setForeground(Color.BLACK);
            } else {
                passwordField.setEchoChar((char) 0); // Reveal text
                toggleButton.setForeground(new Color(0, 120, 215)); // Highlight when visible
            }
        });

        inputWrapper.add(passwordField);
        inputWrapper.add(toggleButton);
        
        groupPanel.add(label);
        groupPanel.add(Box.createVerticalStrut(8));
        groupPanel.add(inputWrapper);
        
        return groupPanel;
    }

    private void validateInputs() {
        String pin = new String(pinField.getPassword());
        String confirm = new String(confirmField.getPassword());

        // Matches exactly 6 digits
        boolean isNumericAndSix = pin.matches("\\d{6}");
        boolean isMatch = pin.equals(confirm);

        nextButton.setEnabled(isNumericAndSix && isMatch);
        
        // Optional: Visual feedback if PINs don't match yet but are 6 digits
        if (isNumericAndSix && !isMatch && confirm.length() >= 6) {
            confirmField.setForeground(Color.RED);
        } else {
            confirmField.setForeground(Color.BLACK);
        }
    }
    
    public void clearFields() {
        pinField.setText("");
        confirmField.setText("");
    }
}