package com.concessions.local.base.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.server.ServerApplication;
import com.concessions.local.ui.ApplicationFrame;


@Component
public class SettingsDialog extends JDialog {

	@Autowired
	private ServerApplication application;
	
	@Autowired
	private ApplicationFrame frame;
	
    public SettingsDialog(JFrame owner, ServerApplication application) {
        super(owner, "Settings", true);
        this.application = application;
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", createGeneralTab());
        tabbedPane.addTab("Maintenance", createMaintenanceTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createGeneralTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Example Setting: Theme Selection
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("UI Theme:"), gbc);
        gbc.gridx = 1;
        panel.add(new JComboBox<>(new String[]{"Light", "Dark", "System"}), gbc);

        return panel;
    }

    private JPanel createMaintenanceTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Reset Section
        JLabel resetHeader = new JLabel("System Reset");
        resetHeader.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(resetHeader, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new JLabel("<html>Restore the application to its original state by clearing all local configurations,<br>"
                + "device mappings, and cached data.</html>"), gbc);

        gbc.gridy++;
        JButton resetButton = new JButton("Factory Reset");
        resetButton.addActionListener(e -> handleFactoryReset());
        panel.add(resetButton, gbc);

        // You can add other maintenance tasks here easily later
        // gbc.gridy++;
        // panel.add(new JSeparator(), gbc);

        gbc.gridy++;
        gbc.weighty = 1.0; // Pushes everything to the top
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private void handleFactoryReset() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "This action is irreversible. All local device IDs, IP mappings, and \n" +
                "offline journals will be deleted. The app will now close.\n\n" +
                "Are you absolutely sure you want to proceed?",
                "Confirm Factory Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Performing system reset...");
            application.reset();
            System.exit(0);
        }
    }

    public static void showSettings(JFrame owner, ServerApplication application) {
        new SettingsDialog(owner, application).setVisible(true);
    }
}