package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.bean.ApplicationConfiguration;
import com.concessions.local.server.model.ApplicationModel;
import com.concessions.local.ui.action.ExitAction;
import com.concessions.local.ui.action.JournalCloseAction;
import com.concessions.local.ui.action.JournalOpenAction;
import com.concessions.local.ui.action.JournalStartAction;
import com.concessions.local.ui.action.JournalSuspendAction;
import com.concessions.local.ui.action.JournalViewAction;
import com.concessions.local.ui.action.KitchenAction;
import com.concessions.local.ui.action.LoginAction;
import com.concessions.local.ui.action.LogoutAction;
import com.concessions.local.ui.action.OrderAction;
import com.concessions.local.ui.action.SetupAction;
import com.concessions.local.ui.controller.JournalController;

import jakarta.annotation.PostConstruct;

@Component
public class ApplicationFrame extends JFrame implements PropertyChangeListener {

	@Autowired
	protected ExitAction exitAction;

	@Autowired
	protected LoginAction loginAction;

	@Autowired
	protected LogoutAction logoutAction;
	
	@Autowired
	protected ApplicationModel appModel;

	protected JournalCloseAction journalCloseAction;

	protected JournalOpenAction journalOpenAction;

	protected JournalStartAction journalStartAction;

	protected JournalSuspendAction journalSuspendAction;

	protected JournalViewAction journalViewAction;

	@Autowired
	protected KitchenAction kitchenAction;

	protected OrderAction orderAction;

	@Autowired
	protected SetupAction setupAction;

	private CardLayout cardLayout = new CardLayout();
	private JLabel statusLabel;
	private JPanel mainContainer = new JPanel(cardLayout);

	public ApplicationFrame() {
		super("Concessions Management System");
	}

	protected JMenuBar initializeMenuBar() {
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

		/*
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
		JMenuItem kitchenItem = new JMenuItem(kitchenAction);
		orderMenu.add(kitchenItem);
	    */

		return menuBar;
	}

	protected JPanel initializeStatusBar() {
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

	@PostConstruct
	private void initializeUI() {
		// Set up the main frame
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout(5, 5));

		JMenuBar menuBar = initializeMenuBar();
		if (menuBar != null) {
			setJMenuBar(initializeMenuBar());
		}

		add(mainContainer, BorderLayout.CENTER);
		
		JPanel statusBar = initializeStatusBar();
		if (statusBar != null) {
			add(statusBar, BorderLayout.SOUTH);
		}

		setSize(800, 600);
		setLocationRelativeTo(null);
	}
	
	public void addPanel (JPanel panel, String name) {
		mainContainer.add(panel, name);
	}
	
	public void showPanel (String name) {
		cardLayout.show(mainContainer, name);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == appModel && evt.getPropertyName().equals(ApplicationModel.PROPERTY_STATUS)) {
			statusLabel.setText(evt.getNewValue().toString());
		}
	}
}
