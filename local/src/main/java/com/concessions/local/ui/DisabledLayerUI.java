package com.concessions.local.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

public class DisabledLayerUI extends LayerUI<JPanel> {
	
	protected String message = "Disabled";

	public DisabledLayerUI() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        if (!c.isEnabled()) {
            // Get the graphics context and apply an alpha filter for a disabled look
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255, 255, 255, 150)); // Light semi-transparent overlay
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());

            // Add the "Disabled" message
            g2.setColor(Color.RED); // Set color for the text
            g2.setFont(new Font("Arial", Font.BOLD, 24)); // Set font
            FontMetrics metrics = g2.getFontMetrics();
            int x = (c.getWidth() - metrics.stringWidth(message)) / 2;
            int y = ((c.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g2.drawString(message, x, y);

            g2.dispose();
        }
    }
	
	@Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        ((JLayer) c).setLayerEventMask(0);
    }

    // Intercept mouse events to block interaction when disabled
    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        if (!l.isEnabled()) {
            e.consume(); // Consume the event, effectively disabling interaction
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        if (!l.isEnabled()) {
            e.consume(); // Consume the event
        }
    }
    
	public void setMessage (String message) {
		this.message = message;
	}
}
