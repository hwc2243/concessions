package com.concessions.local.base.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.EmptyBorder;


public class AboutDialog extends JDialog {
	
	protected AboutDialog (JFrame owner, String applicationName, String applicationVersion)
	{
		super(owner, "", true);
		setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Display the logo
        try {
            // Load the image resource from the classpath (src/main/resources)
            URL imageUrl = getClass().getResource("/logo.png");
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                
             // Get the Image, scale it to 64x64 pixels using smooth scaling
                Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                
                // Create a new ImageIcon from the scaled image
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                
                JLabel imageLabel = new JLabel(scaledIcon);
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // Add the image and a vertical strut above the application name
                contentPanel.add(imageLabel);
                contentPanel.add(Box.createVerticalStrut(15));
            } else {
                // Fallback or error logging if the image is not found
                System.err.println("Warning: logo.png not found in classpath.");
            }
        } catch (Exception e) {
            System.err.println("Error loading logo image: " + e.getMessage());
        }
        
        // Display Application Name
        JLabel nameLabel = new JLabel(applicationName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Display Application Version
        JLabel versionLabel = new JLabel("Version: " + applicationVersion);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copyrightLabel = new JLabel("\u00a9 2025 H William Connors II");
        copyrightLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(nameLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(copyrightLabel);

        add(contentPanel, BorderLayout.CENTER);
        
        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
	}
	
	public static void showAboutDialog (JFrame owner, String applicationName, String applicationVersion) {
		AboutDialog aboutDialog = new AboutDialog(owner, applicationName, applicationVersion);

		aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(owner);
        aboutDialog.setVisible(true);
	}
}
