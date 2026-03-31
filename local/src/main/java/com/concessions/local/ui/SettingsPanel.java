package com.concessions.local.ui;

import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.bean.ClientConfiguration;
import com.formdev.flatlaf.FlatClientProperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SettingsPanel extends JPanel {

	private ApplicationConfiguration appConfig;
	private ClientConfiguration clientConfig;
	
    public SettingsPanel(ApplicationConfiguration appConfig, ClientConfiguration clientConfig) {
    	this.appConfig = appConfig;
    	this.clientConfig = clientConfig;
    	
    	initializeUI();
    }
    
    protected void initializeUI () {
        setLayout(new BorderLayout());
        
        // Main content container (Equivalent to ListView/Column)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header Section (Equivalent to first ListTile)
        JLabel headerLabel = new JLabel("System Settings", SwingConstants.LEFT);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 18f));
        headerLabel.putClientProperty(FlatClientProperties.STYLE, "margin: 0,0,8,0");
        
        content.add(headerLabel);
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(16));

        // System Details Section
        content.add(createSectionHeader("System Details"));
        content.add(createRawInfoRow("System Role:", appConfig.getApplicationRole().toString()));
        content.add(Box.createVerticalStrut(16));
        
        // Location Details Section
        content.add(createSectionHeader("Location Details"));
        content.add(createRawInfoRow("Organization:", appConfig.getLocationConfiguration().getOrganizationName()));
        content.add(createRawInfoRow("Location:", appConfig.getLocationConfiguration().getLocationName()));
        content.add(createRawInfoRow("Menu:", appConfig.getLocationConfiguration().getMenuName()));
        content.add(Box.createVerticalStrut(16));

        // Network Details Section
        content.add(createSectionHeader("Network Details"));
        content.add(createRawInfoRow("Terminal Number:", appConfig.getDeviceNumber()));
        content.add(createRawInfoRow("Client ID:", appConfig.getDeviceId()));
        content.add(createRawInfoRow("Client Address:", clientConfig.getClientIp() + ":" + clientConfig.getClientPort()));
        content.add(createRawInfoRow("Server Address:", clientConfig.getServerIp() + ":" + clientConfig.getServerPort()));

        content.add(Box.createVerticalStrut(12));

        // Wrap in a scroll pane 
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(100, 115, 130)); // Blue-Grey color
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createRawInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblName = new JLabel(label);
        lblName.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(lblValue.getFont().deriveFont(Font.BOLD));

        row.add(lblName, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
    }
}