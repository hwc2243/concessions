package com.concessions.local.ui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.concessions.client.model.Order;
import com.concessions.local.ui.CurrencyRenderer;

public class JournalOrdersPanel extends JPanel {
	private final JTable orderTable;
	private final OrdersListTableModel tableModel;
	    
	public JournalOrdersPanel(List<Order> orders) {
		setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize the table model
        this.tableModel = new OrdersListTableModel(orders);
        this.orderTable = new JTable(tableModel);

        // Apply custom renderer for currency visualization
        orderTable.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());
        
        // Center the header text
        ((DefaultTableCellRenderer)orderTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Make the table look nicer
        orderTable.setRowHeight(25);
        orderTable.setFillsViewportHeight(true);
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Add the table to a JScrollPane for scrolling and headers
        JScrollPane scrollPane = new JScrollPane(orderTable);
        
        // Add a title
        JLabel titleLabel = new JLabel("Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
	}

	class OrdersListTableModel extends AbstractTableModel {
	    private final List<Order> orders;
	    private final String[] columnNames = {
	        "Order Total", "Item Count"
	    };

	    public OrdersListTableModel(List<Order> orders) {
	        this.orders = orders;
	    }

	    @Override
	    public int getRowCount() {
	        return orders.size();
	    }

	    @Override
	    public int getColumnCount() {
	        return columnNames.length;
	    }

	    @Override
	    public String getColumnName(int columnIndex) {
	        return columnNames[columnIndex];
	    }

	    @Override
	    public Object getValueAt(int rowIndex, int columnIndex) {
	        Order order = orders.get(rowIndex);
	        return switch (columnIndex) {
	            case 0 -> order.getOrderTotal();
	            case 1 -> order.getOrderItems().size(); // Calculate the count of order items
	            default -> null;
	        };
	    }

	    @Override
	    public Class<?> getColumnClass(int columnIndex) {
	        return switch (columnIndex) {
	            case 0 -> BigDecimal.class;  // Order Total
	            case 1 -> Integer.class;     // Item Count
	            default -> Object.class;
	        };
	    }
	}
}
