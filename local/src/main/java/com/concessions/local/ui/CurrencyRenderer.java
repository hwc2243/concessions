package com.concessions.local.ui;

import java.awt.Component;
import java.math.BigDecimal;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CurrencyRenderer extends DefaultTableCellRenderer {
    public CurrencyRenderer() {
        setHorizontalAlignment(RIGHT); // Align currency to the right
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof BigDecimal) {
            value = String.format("$%,.2f", (BigDecimal) value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}