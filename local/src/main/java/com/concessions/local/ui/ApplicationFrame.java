package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;

import jakarta.annotation.PostConstruct;

@Component
public class ApplicationFrame extends JFrame implements PropertyChangeListener{

	@Autowired
	protected LoginAction loginAction;
	
	@Autowired
	protected LogoutAction logoutAction;
	
	private JLabel statusLabel;
	
	public ApplicationFrame() {
		super("Concessions Management System");
		//initializeUI();
	}

	private JMenuBar initializeMenuBar() {
		JMenuBar menuBar = new javax.swing.JMenuBar();
		JMenu fileMenu = new javax.swing.JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem loginItem = new JMenuItem(loginAction);
		JMenuItem logoutItem = new JMenuItem(logoutAction);
		fileMenu.add(loginItem);
		fileMenu.add(logoutItem);
		fileMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem(new ExitAction());
		fileMenu.add(exitItem);

		return menuBar;
	}
	
	@PostConstruct
	private void initializeUI() {
		// Set up the main frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setLayout(new BorderLayout(5, 5));
		setJMenuBar(initializeMenuBar());

		// Initialize the Label (the content of the status bar)
		statusLabel = new JLabel("Initializing...");
		statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
		statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8)); // Add padding

		// 2. Create the Status Bar Panel (container for the label)
		JPanel statusBar = new JPanel(new BorderLayout());
		statusBar.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), // Top
																													// separator
																													// line
						BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		statusBar.setBackground(Color.WHITE);

		// 3. Add the label to the status bar panel (aligned LEFT by default in
		// BorderLayout)
		statusBar.add(statusLabel, BorderLayout.WEST);

		// 4. Add the status bar panel to the bottom of the JFrame
		add(statusBar, BorderLayout.SOUTH);

		// --- Main Content (Placeholder) ---
		// Placing a placeholder panel in the CENTER region
		// to show where your main working area or QR code display would go.
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.add(new JLabel("Welcome to the Concession Management System"));
		add(mainContentPanel, BorderLayout.CENTER);

		// Center the frame on the screen
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void propertyChange (PropertyChangeEvent evt) {
		if ("statusMessage".equals(evt.getPropertyName())) {
			String newMessage = (String) evt.getNewValue();
			statusLabel.setText(newMessage);
		}
	}
}
