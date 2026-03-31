package com.concessions.local.kitchen.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.concessions.dto.OrderDTO;

@Component
public class KitchenModel {
    public static final String ORDERS_PROPERTY = "orders";

    private List<OrderDTO> orders = new ArrayList<>();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void addOrder(OrderDTO order) {
        List<OrderDTO> oldOrders = new ArrayList<>(this.orders);
        if (!this.orders.contains(order)) {
        	this.orders.add(order);
        	support.firePropertyChange(ORDERS_PROPERTY, oldOrders, this.orders);
        }
    }

    public void removeOrder(OrderDTO order) {
        List<OrderDTO> oldOrders = new ArrayList<>(this.orders);
        this.orders.remove(order);
        support.firePropertyChange(ORDERS_PROPERTY, oldOrders, this.orders);
    }
}
