package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.concessions.local.Application;
import com.concessions.model.Location;
import com.concessions.model.Menu;
import com.concessions.model.Organization;

public class SetupDialog extends JDialog {

	private final Application application;
	
	private JComboBox<Organization> orgComboBox;
	private JComboBox<Location> locationComboBox;
	private JComboBox<Menu> menuComboBox;
	
	public SetupDialog (Application application) {
		super(application, "Setup", Dialog.ModalityType.APPLICATION_MODAL);
		this.application = application;
		initializeDialog();
	}

	private JPanel createSelectionGroup(String labelText, JComboBox<?> comboBox) {
        // Use BoxLayout (Y_AXIS) for vertical stacking
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        
        // Set up the label
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Set up the combo box
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Ensure the combobox respects the maximum size for cleaner stacking
        comboBox.setMaximumSize(new Dimension(250, 30)); 
        
        groupPanel.add(label);
        groupPanel.add(Box.createVerticalStrut(5)); // Small vertical space
        groupPanel.add(comboBox);
        
        return groupPanel;
    }
	
	private void initializeDialog() {
		setLayout(new BorderLayout(10, 10));
        setSize(400, 300); // Increased size slightly to accommodate vertical stacking
        setLocationRelativeTo(application); // Corrected field name
        setResizable(false);
        
        // 1. Panel to hold both Org selection and Location selection (stacked vertically)
        JPanel selectionContainer = new JPanel();
        selectionContainer.setLayout(new BoxLayout(selectionContainer, BoxLayout.Y_AXIS));
        selectionContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // --- Organization Selection Group ---
        orgComboBox = new JComboBox<>();
        orgComboBox.setPreferredSize(new Dimension(250, 30));
        orgComboBox.setEnabled(false);
        // Add listener to enable location selection once organization is chosen
        /*
        orgComboBox.addActionListener(e -> {
            Organization selectedOrg = (Organization) orgComboBox.getSelectedItem();
            if (selectedOrg != null) {
                // TODO: Replace with actual location fetching logic
                locationComboBox.setEnabled(true);
            } else {
                locationComboBox.setEnabled(false);
                locationComboBox.removeAllItems();
            }
        });
        */

        JPanel orgPanel = createSelectionGroup("Choose Organization:", orgComboBox);
        selectionContainer.add(orgPanel);
        selectionContainer.add(Box.createVerticalStrut(20)); // Larger spacer between groups

        // --- Location Selection Group ---
        locationComboBox = new JComboBox<>();
        locationComboBox.setPreferredSize(new Dimension(250, 30));
        locationComboBox.setEnabled(false); // Disabled until organization is chosen

        JPanel locationPanel = createSelectionGroup("Choose Location:", locationComboBox);
        selectionContainer.add(locationPanel);
        selectionContainer.add(Box.createVerticalStrut(20)); // Larger spacer between groups
        
        menuComboBox = new JComboBox<>();
        menuComboBox.setPreferredSize(new Dimension(250, 30));
        menuComboBox.setEnabled(false); // Disabled until location is chosen
        
        JPanel menuPanel = createSelectionGroup("Choose Menu:", menuComboBox);
        selectionContainer.add(menuPanel);
        
        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            Organization selectedOrg = (Organization) orgComboBox.getSelectedItem();
            if (selectedOrg != null) {
				application.setSelectedOrganization(selectedOrg);
			}
            JOptionPane.showMessageDialog(this, 
                "Selected Organization: " + selectedOrg.getName() + " (ID: " + selectedOrg.getId() + ") set successfully.", 
                "Selection Result", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        
        add(selectionContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
	
	public void addOrganizationSelectionListener (ActionListener listener) {
		orgComboBox.addActionListener(listener);
	}
	
	public void setOrganizations (List<Organization> organizations) {
        // Convert List to array for JComboBox model
        Organization[] orgArray = organizations.toArray(new Organization[0]);
        System.out.println("organizations: " + orgArray.length);
        orgComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(orgArray));
        orgComboBox.setEnabled(true);
        
		revalidate();
		repaint();
	}
	
	public void addLocationSelectionListener (ActionListener listener) {
		locationComboBox.addActionListener(listener);
	}
	
	public void setLocations (List<Location> locations) {
		// Convert List to array for JComboBox model
		Location[] locArray = locations.toArray(new Location[0]);
		System.out.println("locations: " + locArray.length);
		locationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(locArray));
		locationComboBox.setEnabled(true);
		
		revalidate();
		repaint();
	}
	
	public void setMenus (List<Menu> menus) {
		// Convert List to array for JComboBox model
		Menu[] menuArray = menus.toArray(new Menu[0]);
		System.out.println("menus: " + menuArray.length);
		menuComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(menuArray));
		menuComboBox.setEnabled(true);
		
		revalidate();
		repaint();
	}

}
