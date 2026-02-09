package com.concessions.local.kitchen.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.concessions.dto.OrderDTO;
import com.concessions.dto.OrderItemDTO;

import com.concessions.local.kitchen.controller.OrderDisplayController;

public class OrderView extends JPanel {

    private Timer elapsedTimer;
    private JPopupMenu popupMenu;
    private final OrderDisplayController controller; // New final field for the controller

    public OrderView(OrderDTO order, OrderDisplayController controller) {
        this.controller = controller; // Store the controller instance

        // Change main layout to BorderLayout to support a "sticky footer"
        setLayout(new BorderLayout());

        // Keep the existing outer border and sizing hints
        Border lineWithPadding = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            lineWithPadding
        ));
        setPreferredSize(new Dimension(250, 10));
        setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);

        // Panel for the items that will go inside the scroll pane
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        if (order.getOrderItems() != null) {
            for (OrderItemDTO item : order.getOrderItems()) {
                itemsPanel.add(new JLabel(item.getName()));
            }
        }

        // Wrap the items panel in a scroll pane
        JScrollPane itemsScrollPane = new JScrollPane(itemsPanel);
        itemsScrollPane.setBorder(null);

        // Panel for all time info at the bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        if (order.getStartTs() != null) {
            // Start time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = order.getStartTs().format(formatter);
            bottomPanel.add(new JLabel("Started: " + formattedTime));

            // Elapsed time
            JLabel elapsedTimeLabel = new JLabel("Elapsed: 00:00:00");
            bottomPanel.add(elapsedTimeLabel);

            // Timer to update the elapsed time every second
            elapsedTimer = new Timer(1000, e -> {
                Duration duration = Duration.between(order.getStartTs(), LocalDateTime.now());
                long totalSeconds = duration.getSeconds();
                long hours = totalSeconds / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                long seconds = totalSeconds % 60;
                elapsedTimeLabel.setText(String.format("Elapsed: %02d:%02d:%02d", hours, minutes, seconds));
            });
            elapsedTimer.start();
        }

        // Add main components to the panel
        add(itemsScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Popup menu implementation ---
        popupMenu = new JPopupMenu();
        JMenuItem completeItem = new JMenuItem("Complete");
        completeItem.addActionListener(e -> controller.completeOrder(order)); // Call controller
        popupMenu.add(completeItem);

        // Create a single, reusable listener
        MouseListener popupListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };

        // Recursively add the listener to this component and all its children
        addPopupListenerToAll(this, popupListener);
    }

    /**
     * Recursively adds a MouseListener to a component and all of its children.
     */
    private void addPopupListenerToAll(Component component, MouseListener listener) {
        component.addMouseListener(listener);
        // Special handling for JScrollPane to add listener to its viewport
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.getViewport().addMouseListener(listener);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addPopupListenerToAll(child, listener);
            }
        }
    }

    /**
     * Overridden to stop the timer when the component is removed from the screen,
     * preventing resource leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (elapsedTimer != null) {
            elapsedTimer.stop();
        }
    }
}
