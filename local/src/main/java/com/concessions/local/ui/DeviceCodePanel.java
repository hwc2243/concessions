package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.concessions.local.security.TokenAuthService;
import com.concessions.local.service.QRGeneratorService;

public class DeviceCodePanel extends JPanel {

	public static final String NAME = "DEVICE_CODE";

	private QRGeneratorService qrService;
	
	private DeviceCodeUIListener uiListener;
    private Timer countdownTimer;
    private long secondsRemaining;
	
	private String verificationUri;
	private String deviceCode;
		
	private JLabel qrLabel;
	private JLabel expiresLabel;
	private JTextPane codeText;
	private JTextPane uriText;
	
	public DeviceCodePanel(QRGeneratorService qrService, DeviceCodeUIListener listener) {
		this.qrService = qrService;
		this.uiListener = listener;
		initializeUI();
	}

	protected void initializeUI() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

		// Title
		JLabel title = new JLabel("Please Authorize This Device", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 22));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(title);
		contentPanel.add(Box.createVerticalStrut(25));

		// QR Code
		qrLabel = new JLabel();
		qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(qrLabel);
		contentPanel.add(Box.createVerticalStrut(25));

		// Instructions 1
		JLabel uriLabel = new JLabel("Open this URL in your browser:", SwingConstants.CENTER);
		uriLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		uriLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(uriLabel);

		// URI Text
		uriText = createCenteredTextPane(new Font("SansSerif", Font.PLAIN, 14), Color.BLUE);
		uriText.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uriText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				try {
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						Desktop.getDesktop().browse(new java.net.URI(verificationUri));
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(DeviceCodePanel.this, "Could not open browser: " + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPanel.add(uriText);
		contentPanel.add(Box.createVerticalStrut(20));

		// Instructions 2
		JLabel codeLabel = new JLabel("Then enter this code:", SwingConstants.CENTER);
		codeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(codeLabel);

		// Device Code
		codeText = createCenteredTextPane(new Font("SansSerif", Font.BOLD, 36), new Color(0, 128, 0));
		contentPanel.add(codeText);
		contentPanel.add(Box.createVerticalStrut(20));

		// Timer/Instruction
		expiresLabel = new JLabel("This code will expire in m:s.", SwingConstants.CENTER);
		expiresLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
		expiresLabel.setForeground(Color.GRAY);
		expiresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(expiresLabel);

		add(contentPanel, BorderLayout.CENTER);

		// Footer
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(Color.WHITE);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
            stopTimer();
            if (uiListener != null) uiListener.onCancel();
        });
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Helper to create a JTextPane that mimics a Label but allows selection/links 
	 * and remains centered.
	 */
	private JTextPane createCenteredTextPane(Font font, Color color) {
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBackground(Color.WHITE);
		textPane.setFont(font);
		textPane.setForeground(color);
		textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		textPane.setMaximumSize(new java.awt.Dimension(600, 50));
		
		// Center the text within the TextPane itself
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		return textPane;
	}

	public void setResponse (TokenAuthService.DeviceCodeResponse response) {
		this.verificationUri = response.verification_uri();
		this.deviceCode = response.user_code();
		this.secondsRemaining = response.expires_in();
		
		String verificationUrl = response.verification_uri() + "?user_code=" + response.user_code();
		ImageIcon qrIcon = qrService.generateQRCode(verificationUrl, 200, 200);
		if (qrIcon != null) {
			qrLabel.setIcon(qrIcon);
		}
		uriText.setText(verificationUri);
		codeText.setText(deviceCode);
		expiresLabel.setText("This code will expire in " + (response.expires_in() / 60) + " minutes.");
		startCountdown();
	}
	
	private void startCountdown() {
        stopTimer(); // Reset if already running
        countdownTimer = new Timer(1000, e -> {
            secondsRemaining--;
            if (secondsRemaining <= 0) {
                expiresLabel.setText("Code expired.");
                stopTimer();
            } else {
                long mins = secondsRemaining / 60;
                long secs = secondsRemaining % 60;
                expiresLabel.setText(String.format("This code will expire in %d:%02d", mins, secs));
            }
        });
        countdownTimer.start();
    }

    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }
    
	public interface DeviceCodeUIListener {
		void onCancel();
	}
}