package com.concessions.local.kitchen.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel; // New import
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.dto.OrderDTO;
import com.concessions.local.kitchen.controller.KitchenController;
import com.concessions.local.kitchen.model.OrderDisplayModel;

import jakarta.annotation.PostConstruct;

@Component
public class OrderDisplay extends JPanel implements PropertyChangeListener {

    @Autowired
    private OrderDisplayModel model;

    @Autowired
    private KitchenController orderDisplayController; // Inject the controller

    private FillViewportPanel orderPanel;
    private JLabel orderCountLabel; // New instance variable

    @PostConstruct
    public void initialize() {
        setLayout(new BorderLayout());
        // Use the custom FillViewportPanel to ensure child components fill the viewport vertically.
        orderPanel = new FillViewportPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.X_AXIS));

        JScrollPane scrollPane = new JScrollPane(orderPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        // Initialize and add the order count label to the bottom
        orderCountLabel = new JLabel("Total Orders: " + model.getOrders().size());
        orderCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding
        add(orderCountLabel, BorderLayout.SOUTH);

        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (OrderDisplayModel.ORDERS_PROPERTY.equals(evt.getPropertyName())) {
            updateOrderPanel((List<OrderDTO>) evt.getNewValue());
            // Update the order count label when the model changes
            orderCountLabel.setText("Total Orders: " + model.getOrders().size());
        }
    }

    private void updateOrderPanel(List<OrderDTO> orders) {
        orderPanel.removeAll();
        for (OrderDTO order : orders) {
            // Pass the controller instance to the OrderView
            orderPanel.add(new OrderView(order, this.orderDisplayController));
        }
        orderPanel.revalidate();
        orderPanel.repaint();
    }
}
