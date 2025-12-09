package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.model.OrganizationConfiguration;
import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.JournalCloseAction;
import com.concessions.local.ui.action.JournalOpenAction;
import com.concessions.local.ui.action.JournalStartAction;
import com.concessions.local.ui.action.JournalSuspendAction;
import com.concessions.local.ui.action.JournalViewAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.model.ApplicationModel;

import jakarta.annotation.PostConstruct;

@Component
public class ApplicationFrame extends JFrame implements PropertyChangeListener{

	@Autowired
	protected ExitAction exitAction;
	
	@Autowired
	protected LoginAction loginAction;
	
	@Autowired
	protected LogoutAction logoutAction;
	
	@Autowired
	protected JournalCloseAction journalCloseAction;
	
	@Autowired
	protected JournalOpenAction journalOpenAction;
	
	@Autowired
	protected JournalStartAction journalStartAction;

	@Autowired
	protected JournalSuspendAction journalSuspendAction;
	
	@Autowired
	protected JournalViewAction journalViewAction;
	
	
	@Autowired 
	protected OrderAction orderAction;
	
	@Autowired
	protected SetupAction setupAction;
	
	private JLabel orgDisplayLabel;
	private JLabel locationDisplayLabel;
	private JLabel menuDisplayLabel;
	private JLabel statusLabel;
	
	private JPanel mainContentPanel;
	
	public ApplicationFrame() {
		super("Concessions Management System");

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.actionPerformed(new ActionEvent(
                    ApplicationFrame.this, 
                    ActionEvent.ACTION_PERFORMED, 
                    null
                ));
            }
        });
	}

	private JPanel initializeCurrentSetupPanel() {
		// Use GridLayout(1, 3) for one row and three equal columns with 10px horizontal gap
	    JPanel currentSetupPanel = new JPanel(new GridLayout(1, 3, 10, 0));
	    
	    // Add some vertical padding and a background color
	    currentSetupPanel.setBorder(
	    		BorderFactory.createCompoundBorder(
	    				BorderFactory.createEmptyBorder(10, 5, 10, 5), // Outer padding
	    				BorderFactory.createLineBorder(new Color(220, 220, 220)) // Light border
	    		));
	    currentSetupPanel.setBackground(new Color(245, 245, 245)); // Very light gray background

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
	    currentSetupPanel.add(orgDisplayLabel);
	    currentSetupPanel.add(locationDisplayLabel);
	    currentSetupPanel.add(menuDisplayLabel);

	    return currentSetupPanel;
	}
	
	private JMenuBar initializeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem setupItem = new JMenuItem(setupAction);
		fileMenu.add(setupItem);
		fileMenu.addSeparator();
		
		JMenuItem loginItem = new JMenuItem(loginAction);
		JMenuItem logoutItem = new JMenuItem(logoutAction);
		fileMenu.add(loginItem);
		fileMenu.add(logoutItem);
		fileMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem(exitAction);
		fileMenu.add(exitItem);

		JMenu journalMenu = new JMenu("Journal");
		menuBar.add(journalMenu);
		JMenuItem journalViewItem = new JMenuItem(journalViewAction);
		journalMenu.add(journalViewItem);
		JMenuItem journalStartItem = new JMenuItem(journalStartAction);
		journalMenu.add(journalStartItem);
		JMenuItem journalOpenItem = new JMenuItem(journalOpenAction);
		journalMenu.add(journalOpenItem);
		JMenuItem journalSuspendItem = new JMenuItem(journalSuspendAction);
		journalMenu.add(journalSuspendItem);
		JMenuItem journalCloseItem = new JMenuItem(journalCloseAction);
		journalMenu.add(journalCloseItem);
		
		JMenu orderMenu = new JMenu("Order");
		menuBar.add(orderMenu);
		
		JMenuItem orderItem = new JMenuItem(orderAction);
		orderMenu.add(orderItem);
		
		return menuBar;
	}
	
	@PostConstruct
	private void initializeUI() {
		// Set up the main frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setLayout(new BorderLayout(5, 5));
		setJMenuBar(initializeMenuBar());

		JPanel currentSetupPanel = initializeCurrentSetupPanel();
		mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainContentPanel.add(new JLabel("Welcome to the Concession Management System"));
		
		JPanel contentWrapperPanel = new JPanel(new BorderLayout(5, 5));
		
		contentWrapperPanel.add(currentSetupPanel, BorderLayout.SOUTH);
		contentWrapperPanel.add(mainContentPanel, BorderLayout.CENTER);
		
		add(contentWrapperPanel, BorderLayout.CENTER);

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

		add(statusBar, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Clears the existing content in the main content area and displays the new panel.
	 * * @param contentPanel The JPanel (e.g., ConcessionOrderPanel) to display.
	 */
	public void setMainContent(JPanel contentPanel) {
		mainContentPanel.removeAll();
		mainContentPanel.add(contentPanel, BorderLayout.CENTER);
		mainContentPanel.revalidate();
		mainContentPanel.repaint();
	}

	@Override
	public void propertyChange (PropertyChangeEvent evt) {
		if (ApplicationModel.STATUS_MESSAGE.equals(evt.getPropertyName())) {
			String newMessage = (String) evt.getNewValue();
			statusLabel.setText(newMessage);
		} else if (ApplicationModel.ORGANIZATION_CONFIGURATION.equals(evt.getPropertyName())) {
			OrganizationConfiguration organizationConfiguration = (OrganizationConfiguration)evt.getNewValue();
			orgDisplayLabel.setText("Organization: " + organizationConfiguration.getOrganizationName());
			locationDisplayLabel.setText("Location: " + organizationConfiguration.getLocationName());
			menuDisplayLabel.setText("Menu: " + organizationConfiguration.getMenuName());
		}
	}
}
