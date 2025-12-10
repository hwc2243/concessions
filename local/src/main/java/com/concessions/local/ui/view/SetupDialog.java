package com.concessions.local.ui.view;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

import com.concessions.local.server.ServerApplication;
import com.concessions.local.ui.model.SetupModel;
import com.concessions.client.model.Location;
import com.concessions.client.model.Menu;
import com.concessions.client.model.Organization;

@Component
public class SetupDialog extends JDialog {

	public static final String PLEASE_SELECT = "Please select...";
	
	private SetupModel model;
	
	private JComboBox<Organization> orgComboBox;
	private JComboBox<Location> locationComboBox;
	private JComboBox<Menu> menuComboBox;
	private JPasswordField pinField;
	private JPasswordField confirmField;
	private JButton setupButton;
	private JButton cancelButton;
	
	public SetupDialog (SetupModel model) {
		super(null, "Setup", Dialog.ModalityType.APPLICATION_MODAL);
		//this.application = application;
		this.model = model;
		
		initializeDialog();
	}

	private JPanel createSelectionGroup(String labelText, JComboBox<?> comboBox) {
        // Use BoxLayout (Y_AXIS) for vertical stacking
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
        
        // Set up the label
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        // Set up the combo box
        comboBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        // Ensure the combobox respects the maximum size for cleaner stacking
        comboBox.setMaximumSize(new Dimension(250, 30)); 
        
        groupPanel.add(label);
        groupPanel.add(Box.createVerticalStrut(5)); // Small vertical space
        groupPanel.add(comboBox);
        
        return groupPanel;
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

        // --- Organization Selection Group ---
        orgComboBox = new JComboBox<>();
        orgComboBox.setPreferredSize(new Dimension(250, 30));
        orgComboBox.setEnabled(false);

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
        
        // --- Menu selection Group
        menuComboBox = new JComboBox<>();
        menuComboBox.setPreferredSize(new Dimension(250, 30));
        menuComboBox.setEnabled(false); // Disabled until location is chosen
        
        JPanel menuPanel = createSelectionGroup("Choose Menu:", menuComboBox);
        selectionContainer.add(menuPanel);
        selectionContainer.add(Box.createVerticalStrut(20)); // Larger spacer between groups
        
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
        setupButton = new JButton("Setup");
        setupButton.setActionCommand(OK_COMMAND);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(CANCEL_COMMAND);
        
        buttonPanel.add(setupButton);
        buttonPanel.add(cancelButton);
        
        add(selectionContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        model.addPropertyChangeListener(evt -> {
			switch (evt.getPropertyName()) {
			case "organizations":
				setOrganizations(model.getOrganizations());
				break;
			case "locations":
				setLocations(model.getLocations());
				break;
			case "menus":
				setMenus(model.getMenus());
				break;
			}
		});
    }
	
	public void addOrganizationSelectionListener (ActionListener listener) {
		orgComboBox.addActionListener(listener);
	}
	
	public Organization getSelectedOrganization () {
		return (Organization)orgComboBox.getSelectedItem();
	}
	
	public void setOrganizations (List<Organization> organizations) {
		List<Organization> displayList = new ArrayList<>();
		displayList.add(new OrganizationPlaceholder()); 
		
		if (organizations != null) {
			displayList.addAll(organizations);
		}
		
		Organization[] orgArray = displayList.toArray(new Organization[0]);
		System.out.println("organizations: " + orgArray.length);
		orgComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(orgArray));
		orgComboBox.setEnabled(true);
		orgComboBox.setSelectedIndex(0);

		revalidate();
		repaint();
	}
	
	public void addLocationSelectionListener (ActionListener listener) {
		locationComboBox.addActionListener(listener);
	}
	
	public Location getSelectedLocation() {
		return (Location) locationComboBox.getSelectedItem();
	}
	
	public void setLocations (List<Location> locations) {
		List<Location> displayList = new ArrayList<>();
		displayList.add(new LocationPlaceholder()); 
		
		if (locations != null) {
			displayList.addAll(locations);
		}
		
		// Convert List to array for JComboBox model
		Location[] locArray = displayList.toArray(new Location[0]);
		System.out.println("locations: " + locArray.length);
		locationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(locArray));
		locationComboBox.setEnabled(true);
		
		revalidate();
		repaint();
	}
	
	public void addMenuSelectionListener (ActionListener listener) {
		menuComboBox.addActionListener(listener);
	}
	
	public Menu getSelectedMenu() {
		return (Menu) menuComboBox.getSelectedItem();
	}
	
	public void setMenus (List<Menu> menus) {
		List<Menu> displayList = new ArrayList<>();
		displayList.add(new MenuPlaceholder()); 
		
		if (menus != null) {
			displayList.addAll(menus);
		}
		// Convert List to array for JComboBox model
		Menu[] menuArray = displayList.toArray(new Menu[0]);
		System.out.println("menus: " + menuArray.length);
		menuComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(menuArray));
		menuComboBox.setEnabled(true);
		
		revalidate();
		repaint();
	}
	
	public void setSetupEnabled (boolean enabled) {
		setupButton.setEnabled(enabled);
	}
	
	public void addActionListener (ActionListener listener) {
		setupButton.addActionListener(listener);
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
    
	private static class OrganizationPlaceholder extends Organization {
		@Override
		public String toString() { return PLEASE_SELECT; }
		
		public OrganizationPlaceholder() {
			super();
		}
	}
	
	private static class LocationPlaceholder extends Location {
		@Override
		public String toString() { return PLEASE_SELECT; }
		
		public LocationPlaceholder() {
			super();
		}
	}
	
	private static class MenuPlaceholder extends Menu {
		@Override
		public String toString() { return PLEASE_SELECT; }
		
		public MenuPlaceholder() {
			super();
		}
	}
}
