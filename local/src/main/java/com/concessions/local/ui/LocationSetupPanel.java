package com.concessions.local.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.concessions.local.ui.model.LocationSetupModel;
import com.concessions.client.model.Location;
import com.concessions.client.model.Menu;
import com.concessions.client.model.Organization;

public class LocationSetupPanel extends JPanel {
	
	public static final String NAME = "LOCATION";
	public static final String PLEASE_SELECT = "Please select...";
	
	private final LocationSetupModel model;
	
	private JComboBox<Organization> orgComboBox;
	private JComboBox<Location> locationComboBox;
	private JComboBox<Menu> menuComboBox;
	private JButton nextButton;
	private JButton cancelButton;
	
	public LocationSetupPanel (LocationSetupModel model) {
		this.model = model;
		initializeUI();
		setupModelPropertyListener();
	}

	/**
	 * Configures high-level functional listeners for navigation.
	 */
	public void setupNavigationListeners(Runnable onNext, Runnable onCancel) {
		nextButton.addActionListener(e -> onNext.run());
		cancelButton.addActionListener(e -> onCancel.run());
	}

	private void initializeUI() {
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout()); // Centering wrapper

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(Color.WHITE);
		content.setBorder(new EmptyBorder(30, 40, 30, 40));

		// --- Header ---
		JLabel titleLabel = new JLabel("Location Selection");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel subLabel = new JLabel("Configure where this device will operate.");
		subLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		subLabel.setForeground(Color.GRAY);
		subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// --- Selection Groups ---
		orgComboBox = createStyledComboBox();
		JPanel orgPanel = createSelectionGroup("Organization", orgComboBox);

		locationComboBox = createStyledComboBox();
		JPanel locationPanel = createSelectionGroup("Location", locationComboBox);

		menuComboBox = createStyledComboBox();
		JPanel menuPanel = createSelectionGroup("Menu", menuComboBox);

		// --- Button Footer ---
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		buttonPanel.setBackground(Color.WHITE);
		
		nextButton = new JButton("Next");
		nextButton.setPreferredSize(new Dimension(100, 35));
		nextButton.setEnabled(false); // Default disabled

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 35));

		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);

		// Assembly
		content.add(titleLabel);
		content.add(Box.createVerticalStrut(5));
		content.add(subLabel);
		content.add(Box.createVerticalStrut(30));
		content.add(orgPanel);
		content.add(Box.createVerticalStrut(15));
		content.add(locationPanel);
		content.add(Box.createVerticalStrut(15));
		content.add(menuPanel);
		content.add(Box.createVerticalStrut(30));
		content.add(buttonPanel);

		add(content, new GridBagConstraints());
	}

	private <T> JComboBox<T> createStyledComboBox() {
		JComboBox<T> combo = new JComboBox<>();
		combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
		combo.setPreferredSize(new Dimension(280, 35));
		combo.setMaximumSize(new Dimension(280, 35));
		combo.setAlignmentX(Component.CENTER_ALIGNMENT);
		combo.setEnabled(false);
		
		// Auto-validate "Next" button when a selection changes
		combo.addActionListener(e -> validateSelections());
		return combo;
	}

	private JPanel createSelectionGroup(String labelText, JComboBox<?> comboBox) {
		JPanel group = new JPanel();
		group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
		group.setBackground(Color.WHITE);
		
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("SansSerif", Font.BOLD, 13));
		label.setForeground(new Color(80, 80, 80));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		group.add(label);
		group.add(Box.createVerticalStrut(5));
		group.add(comboBox);
		return group;
	}

	private void validateSelections() {
		boolean menuReady = menuComboBox.getSelectedItem() != null 
				&& !(menuComboBox.getSelectedItem() instanceof MenuPlaceholder);
		nextButton.setEnabled(menuReady);
	}

	private void setupModelPropertyListener() {
		model.addPropertyChangeListener(evt -> {
			switch (evt.getPropertyName()) {
				case "organizations" -> setOrganizations(model.getOrganizations());
				case "locations"     -> setLocations(model.getLocations());
				case "menus"         -> setMenus(model.getMenus());
			}
		});
	}

	// --- Standard Dropdown Updaters ---

	public void setOrganizations(List<Organization> organizations) {
		updateComboBox(orgComboBox, organizations, new OrganizationPlaceholder());
	}

	public void setLocations(List<Location> locations) {
		updateComboBox(locationComboBox, locations, new LocationPlaceholder());
	}

	public void setMenus(List<Menu> menus) {
		updateComboBox(menuComboBox, menus, new MenuPlaceholder());
	}

	private <T> void updateComboBox(JComboBox<T> combo, List<T> data, T placeholder) {
		List<T> displayList = new ArrayList<>();
		displayList.add(placeholder);
		if (data != null) displayList.addAll(data);
		
		combo.setModel(new DefaultComboBoxModel<>((T[]) displayList.toArray()));
		combo.setEnabled(true);
		revalidate();
		repaint();
	}

	// --- Getters ---
	public Organization getSelectedOrganization() { return (Organization) orgComboBox.getSelectedItem(); }
	public Location getSelectedLocation() { return (Location) locationComboBox.getSelectedItem(); }
	public Menu getSelectedMenu() { return (Menu) menuComboBox.getSelectedItem(); }

	// --- Listener Hooks for Controller ---
	public void addOrganizationSelectionListener(java.awt.event.ActionListener l) { orgComboBox.addActionListener(l); }
	public void addLocationSelectionListener(java.awt.event.ActionListener l) { locationComboBox.addActionListener(l); }
	public void addMenuSelectionListener(java.awt.event.ActionListener l) { menuComboBox.addActionListener(l); }

	// --- Placeholder Inner Classes ---
	private static class OrganizationPlaceholder extends Organization {
		@Override public String toString() { return PLEASE_SELECT; }
	}
	private static class LocationPlaceholder extends Location {
		@Override public String toString() { return PLEASE_SELECT; }
	}
	private static class MenuPlaceholder extends Menu {
		@Override public String toString() { return PLEASE_SELECT; }
	}
}