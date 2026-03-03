package com.concessions.local.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class WelcomePanel extends JPanel {

	public static final String NAME = "WELCOME";
	
    public WelcomePanel(String applicationName, String applicationVersion) {
        // Set background to match your clean UI
        setBackground(Color.WHITE);
        
        // Use BoxLayout for vertical centering/stacking
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 1. Icon (Logo)
        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        URL iconUrl = getClass().getResource("/logo.png");
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            // Scale the icon to a standard logo size (e.g., 80x80)
            Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            // Fallback emoji if image is missing during development
            iconLabel.setText("🏪");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
            System.err.println("Resource /icon.png not found!");
        }

        // 2. Application Name
        JLabel nameLabel = new JLabel(applicationName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Application Version
        JLabel versionLabel = new JLabel("Version " + applicationVersion);
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with spacing
        add(iconLabel);
        add(Box.createRigidArea(new Dimension(0, 20))); // Gap after logo
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0, 10))); // Gap after name
        add(versionLabel);
    }
}
