package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.service.QRGeneratorService;

@Component
public class DeviceCodeModal extends JDialog {

	@Autowired
	protected ApplicationFrame applicationFrame;
	
	@Autowired
	protected QRGeneratorService qrService;
	
	protected String verificationUri;
	protected String deviceCode;
	protected long expiresInSeconds;
		
	private JLabel qrLabel;
	private JTextPane codeText;
	private JTextPane uriText;
	
	public DeviceCodeModal() {
		initializeUI();
	}

	protected void initializeUI() {
		setTitle("Authentication Required");
		setLayout(new BorderLayout(10, 10));
		setSize(500, 500);
		//setLocationRelativeTo(this);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // User can dismiss the modal

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel title = new JLabel("Please Authorize This Device", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 16));
		contentPanel.add(title);
		contentPanel.add(Box.createVerticalStrut(10));

		qrLabel = new JLabel();
		qrLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPanel.add(qrLabel);
		contentPanel.add(Box.createVerticalStrut(10));

		// 1. Verification URI link
		JLabel uriLabel = new JLabel("<html><p>Open this URL in your browser:</p></html>");
		contentPanel.add(uriLabel);
		uriText = new JTextPane();
		uriText.setEditable(false);
		uriText.setBackground(Color.LIGHT_GRAY);
		uriText.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uriText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				try {
					// Automatically open the link in the default system browser
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						Desktop.getDesktop().browse(new java.net.URI(verificationUri));
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(DeviceCodeModal.this, "Could not open browser: " + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPanel.add(uriText);
		contentPanel.add(Box.createVerticalStrut(15));

		// 2. User Code
		JLabel codeLabel = new JLabel("<html><p>Then enter this code:</p></html>");
		contentPanel.add(codeLabel);
		codeText = new JTextPane();
		codeText.setFont(new Font("Monospaced", Font.BOLD, 24));
		codeText.setEditable(false);
		codeText.setForeground(new Color(0, 100, 0));
		codeText.setBackground(Color.WHITE);
		contentPanel.add(codeText);
		contentPanel.add(Box.createVerticalStrut(10));

		// Timer/Instruction
		JLabel info = new JLabel("This code will expire in " + (expiresInSeconds / 60) + " minutes.",
				SwingConstants.CENTER);
		contentPanel.add(info);

		add(contentPanel, BorderLayout.CENTER);

		// Cancel button in the modal's footer
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			DeviceCodeModal.this.dispose();
			// statusLabel.setText("Login cancelled by user.");
			// TODO: If necessary, stop the polling scheduler in TokenAuthService
		});
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		setSize(400, 200);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // Center the dialog on the screen
	}
	
	public void showDialog (String verificationUri, String deviceCode, long expiresInSeconds) {
		this.verificationUri = verificationUri;
		this.deviceCode = deviceCode;
		this.expiresInSeconds = expiresInSeconds;
		
		String verificationUrl = verificationUri + "?user_code=" + deviceCode;
		ImageIcon qrIcon = qrService.generateQRCode(verificationUrl, 200, 200);
		if (qrIcon != null) {
			qrLabel.setIcon(qrIcon);
		}
		
		uriText.setText(verificationUri);
		codeText.setText(deviceCode);

		pack();
		setLocationRelativeTo(applicationFrame);
		setVisible(true);
	}
	
}
