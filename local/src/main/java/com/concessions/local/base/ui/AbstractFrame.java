package com.concessions.local.base.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.concessions.local.base.model.AbstractLocationConfigurationModel;
import com.concessions.local.base.model.AbstractModel;
import com.concessions.local.base.ui.AbstractFrame.CurrentConfiguration;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.server.model.ServerApplicationModel;

public abstract class AbstractFrame extends JFrame implements PropertyChangeListener {

	private JLabel orgDisplayLabel;
	private JLabel locationDisplayLabel;
	private JLabel menuDisplayLabel;
	private JLabel statusLabel;
	private JPanel mainContentPanel;
	
	public AbstractFrame(String title) {
		super(title);
	}

	protected void initializeUI () {
		setLayout(new BorderLayout(5, 5));

		JMenuBar menuBar = initializeMenuBar();
		if (menuBar != null) {
			setJMenuBar(initializeMenuBar());
		}

		mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainContentPanel.add(new JLabel("Welcome to the Concession Management System"));
		add(mainContentPanel, BorderLayout.CENTER);

		/*
		JPanel contentWrapperPanel = new JPanel(new BorderLayout());
		contentWrapperPanel.add(mainContentPanel, BorderLayout.CENTER);
		add(contentWrapperPanel, BorderLayout.CENTER);
		*/

		JPanel bottomPanel = new JPanel(new BorderLayout(0,0));
		
		JPanel currentConfigurationPanel = this.initializeCurrentConfigurationPanel();
		if (currentConfigurationPanel != null) {
			bottomPanel.add(currentConfigurationPanel, BorderLayout.NORTH);
		}
		
		JPanel statusBar = initializeStatusBar();
		if (statusBar != null) {
			bottomPanel.add(statusBar, BorderLayout.SOUTH);
		}
		
		if (bottomPanel.getComponentCount() > 0) {
			add(bottomPanel, BorderLayout.SOUTH);
		}
	}
	
	protected JPanel initializeCurrentConfigurationPanel() {
		// Use GridLayout(1, 3) for one row and three equal columns with 10px horizontal gap
	    JPanel currentConfigurationPanel = new JPanel(new GridLayout(1, 3, 10, 0));
	    
	    // Add some vertical padding and a background color
	    currentConfigurationPanel.setBorder(
	    		BorderFactory.createCompoundBorder(
	    				BorderFactory.createEmptyBorder(5, 0, 0, 0), // Outer padding
	    				BorderFactory.createLineBorder(new Color(220, 220, 220)) // Light border
	    		));
	    currentConfigurationPanel.setBackground(new Color(245, 245, 245)); // Very light gray background

	    // Initialize display labels
	    orgDisplayLabel = new JLabel("Organization: N/A", SwingConstants.CENTER);
	    locationDisplayLabel = new JLabel("Location: N/A", SwingConstants.CENTER);
	    menuDisplayLabel = new JLabel("Menu: N/A", SwingConstants.CENTER);
	    
	    // Styling (use a slightly smaller, distinct font for clarity)
	    Font statusFont = new Font("Arial", Font.BOLD, 12);
	    orgDisplayLabel.setFont(statusFont);
	    locationDisplayLabel.setFont(statusFont);
	    menuDisplayLabel.setFont(statusFont);
	    
	    orgDisplayLabel.setForeground(new Color(50, 50, 150)); // Distinct color for Organization
	    locationDisplayLabel.setForeground(new Color(50, 150, 50)); // Distinct color for Location
	    menuDisplayLabel.setForeground(new Color(150, 50, 50)); // Distinct color for Menu

	    // Add labels to the panel
	    currentConfigurationPanel.add(orgDisplayLabel);
	    currentConfigurationPanel.add(locationDisplayLabel);
	    currentConfigurationPanel.add(menuDisplayLabel);

	    return currentConfigurationPanel;
	}
	
	protected JMenuBar initializeMenuBar () {
		return null;
	}
	
	protected JPanel initializeStatusBar () {
		statusLabel = new JLabel("Initializing...");
		statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
		statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8)); // Add padding

		JPanel statusBar = new JPanel(new BorderLayout());
		statusBar.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), // Top
																													// separator
																													// line
						BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		statusBar.setBackground(Color.WHITE);

		statusBar.add(statusLabel, BorderLayout.WEST);
		
		return statusBar;
	}

	protected void updateCurrentConfiguration (CurrentConfiguration currentConfiguration) {
		if (currentConfiguration != null) {
			orgDisplayLabel.setText("Organization: " + currentConfiguration.organizationName());
			locationDisplayLabel.setText("Location: " + currentConfiguration.locationName());
			menuDisplayLabel.setText("Menu: " + currentConfiguration.menuName());
		} else {
			orgDisplayLabel.setText("Organization: N/A");
			locationDisplayLabel.setText("Location: N/A");
			menuDisplayLabel.setText("Menu: N/A");
			
		}
	}
	
	/**
	 * Clears the existing content in the main content area and displays the new panel.
	 * @param contentPanel The JPanel (e.g., ConcessionOrderPanel) to display.
	 */
	public void setMainContent(JPanel contentPanel) {
		mainContentPanel.removeAll();
		//mainContentPanel.setLayout(new GridLayout(1, 1));
		//mainContentPanel.add(contentPanel);
		//mainContentPanel.setLayout(new BorderLaout());
		mainContentPanel.add(contentPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	protected void updateStatus (String status) {
		statusLabel.setText(status);
	}
	
	@Override
	public void propertyChange (PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case AbstractModel.STATUS_MESSAGE:
			String newMessage = (String) evt.getNewValue();
			updateStatus(newMessage);
			break;
		case AbstractLocationConfigurationModel.LOCATION_CONFIGURATION:
			LocationConfiguration locationConfiguration = (LocationConfiguration)evt.getNewValue();
			if (locationConfiguration != null) {
				this.updateCurrentConfiguration(
						new CurrentConfiguration(locationConfiguration.getOrganizationName(),
								locationConfiguration.getLocationName(),
								locationConfiguration.getMenuName()));
			} else {
				this.updateCurrentConfiguration(null);
			}
			break;
		}
	}
	
	public record CurrentConfiguration(
		    String organizationName,
		    String locationName,
		    String menuName
		) {
		}
}
