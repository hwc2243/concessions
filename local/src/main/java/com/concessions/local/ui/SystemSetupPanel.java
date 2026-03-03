package com.concessions.local.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.concessions.local.bean.ApplicationConfiguration.ApplicationRole;

public class SystemSetupPanel extends JPanel {
	public static final String NAME = "SYSTEM_SETUP";
	
	public SystemSetupPanel (SystemSetupListener listener) {
		setBackground(Color.WHITE);
		// Use GridBagLayout to keep everything centered as a single unit
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;

		// This wrapper panel holds all your content
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(Color.WHITE);
		// Reduced top padding to 20 to stop it from pushing off screen
		content.setBorder(new EmptyBorder(20, 40, 20, 40));

		// --- Header Section ---
		/*
		 * JLabel iconLabel = new JLabel("🏪", SwingConstants.CENTER);
		 * iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
		 * iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		 */

		JLabel titleLabel = new JLabel("Welcome to Concessions POS");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel subTitleLabel = new JLabel("Choose how you would like to begin.");
		subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		subTitleLabel.setForeground(Color.GRAY);
		subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// --- Cards ---
		OptionCard setupCard = new OptionCard("Setup a new system", "This device will act as the location server.", e -> listener.onRoleSelected(ApplicationRole.SERVER));
		OptionCard addCard = new OptionCard("Add to existing system",
				"Connect this device to an active location network.", e -> listener.onRoleSelected(ApplicationRole.CLIENT));

		// Add to the internal 'content' box
		/*
		 * content.add(iconLabel); content.add(Box.createRigidArea(new Dimension(0,
		 * 15)));
		 */
		content.add(titleLabel);
		content.add(Box.createRigidArea(new Dimension(0, 5)));
		content.add(subTitleLabel);
		content.add(Box.createRigidArea(new Dimension(0, 30)));
		content.add(setupCard);
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		content.add(addCard);

		// Add the content box to the GridBagLayout (this keeps it centered)
		add(content, gbc);
	}
	
	// Define the interface
    public interface SystemSetupListener {
        void onRoleSelected(ApplicationRole role);
    }
}
