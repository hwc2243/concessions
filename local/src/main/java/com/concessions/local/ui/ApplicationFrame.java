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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.StatusType;
import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.model.LocationConfiguration;
import com.concessions.local.server.model.ServerApplicationModel;
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
import com.concessions.local.ui.controller.JournalController;

import jakarta.annotation.PostConstruct;

@Component
public class ApplicationFrame extends AbstractFrame implements PropertyChangeListener {

	@Autowired
	protected ExitAction exitAction;
	
	@Autowired
	protected LoginAction loginAction;
	
	@Autowired
	protected LogoutAction logoutAction;
	
	@Autowired
	protected JournalController journalController;
	
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
	
	public ApplicationFrame() {
		super("Concessions Management System");

	}
	
	@Override
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
	private void initialize () {
		// Set up the main frame
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.actionPerformed(new ActionEvent(
                    this, 
                    ActionEvent.ACTION_PERFORMED, 
                    null
                ));
            }
        });

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 400);
		
		super.initializeUI();

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
