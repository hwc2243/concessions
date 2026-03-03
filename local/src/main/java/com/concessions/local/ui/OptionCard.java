package com.concessions.local.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OptionCard extends JPanel {
    private int radius = 20;
    private Color hoverColor = new Color(248, 249, 250);

    public OptionCard(String title, String desc, java.awt.event.ActionListener action) {
        setOpaque(false);
        setLayout(new BorderLayout(15, 0));
        setBorder(new javax.swing.border.EmptyBorder(20, 25, 20, 25));
        setMaximumSize(new Dimension(550, 110));
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Labels
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel dLabel = new JLabel("<html>" + desc + "</html>");
        dLabel.setForeground(Color.GRAY);

        JPanel textWrapper = new JPanel();
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));
        textWrapper.setOpaque(false);
        textWrapper.add(tLabel);
        textWrapper.add(dLabel);

        add(textWrapper, BorderLayout.CENTER);
        add(new JLabel("〉"), BorderLayout.EAST); // Simple arrow

        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked (MouseEvent evt) {
        		if (action != null) {
        			action.actionPerformed(new ActionEvent(OptionCard.this, ActionEvent.ACTION_PERFORMED, "RoleSelected"));
        		}
        	}
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(Color.WHITE); repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.setColor(new Color(230, 230, 230));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        g2.dispose();
    }
}