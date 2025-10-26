package com.concessions.local.ui.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.concessions.local.ui.model.OrderModel;
import com.concessions.model.CategoryType;
import com.concessions.model.MenuItem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A three-pane UI component for ordering in a concessions application.
 * 1. Left: Category buttons
 * 2. Center: Menu Item buttons (dynamic based on selected category)
 * 3. Right: Current Order list
 */
public class OrderPanel extends JPanel {

    // --- Data Structures (Mocks) ---
    private record OrderEntry(String itemName, BigDecimal price) {
        @Override
        public String toString() {
            return String.format("%-25s $%s", itemName, price.setScale(2));
        }
    }

    private OrderModel orderModel;
    private Map<CategoryType, List<MenuItem>> menuData;
    
    // --- UI Components ---
    private final JPanel categoryPanel = new JPanel();
    private final JPanel itemCardPanel = new JPanel(new CardLayout()); // Uses CardLayout to swap item panels
    private final DefaultListModel<OrderEntry> orderListModel = new DefaultListModel<>();
    private final JLabel totalLabel = new JLabel("$0.00");
    private final JPanel centerTitlePanel = new JPanel(new BorderLayout(5, 5));
    
    // --- Mock Data ---
    private final Map<String, JPanel> itemPanels = new LinkedHashMap<>(); // To store panels per category

    public OrderPanel (OrderModel orderModel) {
    	this.orderModel = orderModel;
        this.menuData = orderModel.getMenuData();
    	
        // 1. Set up the main layout: Use GridBagLayout for flexible column widths
        setLayout(new GridBagLayout()); 
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Outer padding

        // Setup GridBagConstraints for components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontal and vertical space
        // Use insets to create the 10px horizontal gap between components
        gbc.insets = new Insets(0, 5, 0, 5); 
        gbc.weighty = 1.0; // Allow components to grow vertically
        
        // --- 1. Left Pane (Categories) ---
        gbc.gridx = 0;
        // Narrowest: Weight 1.0 (approx 20% of extra width)
        gbc.weightx = 1.0; 
        add(createLeftPane(), gbc);

        // --- 2. Center Pane (Menu Items) ---
        gbc.gridx = 1;
        // Widest: Weight 2.5 (approx 50% of extra width)
        gbc.weightx = 2.5; 
        add(createCenterPane(), gbc);

        // --- 3. Right Pane (Order) ---
        gbc.gridx = 2;
        // Moderately wide: Weight 1.5 (approx 30% of extra width)
        gbc.weightx = 1.5; 
        add(createRightPane(), gbc);
        
        // Set initial state
        if (!menuData.isEmpty()) {
            CategoryType initialCategory = menuData.keySet().iterator().next();
            
            // Show the first category's items
            CardLayout cl = (CardLayout) (itemCardPanel.getLayout());
            cl.show(itemCardPanel, initialCategory.getName());
            
            // Set the initial center pane title
            centerTitlePanel.setBorder(BorderFactory.createTitledBorder(initialCategory.getName()));
        }
    }
    
    // --- Pane Initialization Methods ---

    private JScrollPane createLeftPane() {
        categoryPanel.setLayout(new GridLayout(0, 1, 5, 5)); // One column, vertical gap
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Categories"));

        for (CategoryType category : menuData.keySet()) {
            JButton categoryButton = new JButton(category.getName());
            categoryButton.setBackground(getCategoryColor(category));
            //categoryButton.setForeground(Color.WHITE);
            categoryButton.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Set action to switch the center panel
            categoryButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) (itemCardPanel.getLayout());
                cl.show(itemCardPanel, category.getName());
                
                centerTitlePanel.setBorder(BorderFactory.createTitledBorder(category.getName() + " Menu Items"));
                centerTitlePanel.revalidate();
                centerTitlePanel.repaint();
            });
            
            categoryPanel.add(categoryButton);
        }
        
        JScrollPane scrollPane = new JScrollPane(categoryPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createCenterPane() {
        
        // Populate the CardLayout with specific panels for each category
        for (Map.Entry<CategoryType, List<MenuItem>> entry : menuData.entrySet()) {
            CategoryType category = entry.getKey();
            List<MenuItem> items = entry.getValue();
            
            // Create a panel for the items of this category
            JPanel categoryItemPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 columns for items
            
            for (MenuItem item : items) {
                JButton itemButton = new JButton("<html><center>" + item.getName() + "<br>($" + item.getPrice().setScale(2) + ")</center></html>");
                itemButton.setBackground(new Color(240, 240, 240));
                itemButton.setFont(new Font("Arial", Font.PLAIN, 14));
                
                // Set action to add the item to the order list
                itemButton.addActionListener(new AddItemToOrderAction(item));
                
                categoryItemPanel.add(itemButton);
            }
            JPanel wrapperPanel = new JPanel(new BorderLayout());
            wrapperPanel.add(categoryItemPanel, BorderLayout.NORTH);
            
            // Add the category-specific panel to the CardLayout container
            itemCardPanel.add(wrapperPanel, category.getName());
        }

        JScrollPane scrollPane = new JScrollPane(itemCardPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        centerTitlePanel.add(scrollPane, BorderLayout.CENTER);
        
        return centerTitlePanel;
    }

    private JPanel createRightPane() {
        JPanel orderPane = new JPanel(new BorderLayout(5, 5));
        orderPane.setBorder(BorderFactory.createTitledBorder("Current Order"));

        // 1. Order List
        JList<OrderEntry> orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(orderList);
        
        // 2. Footer (Total & Checkout)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalPanel.add(new JLabel("Total: "));
        totalPanel.add(totalLabel);
        
        // Checkout Button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        //checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Order Total: " + totalLabel.getText() + "\nProcessing checkout...", 
                "Checkout Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            orderListModel.clear();
            updateTotal();
        });

        footerPanel.add(totalPanel, BorderLayout.NORTH);
        footerPanel.add(checkoutButton, BorderLayout.SOUTH);
        
        orderPane.add(scrollPane, BorderLayout.CENTER);
        orderPane.add(footerPanel, BorderLayout.SOUTH);

        return orderPane;
    }

	private Color getCategoryColor(CategoryType type) {
        return switch (type) {
            case APPETIZER -> new Color(255, 140, 0);   // Dark Orange
            case ENTREE -> new Color(60, 179, 113);     // Medium Sea Green
            case SIDE -> new Color(255, 215, 0);        // Gold
            case DRINK -> new Color(70, 130, 180);      // Steel Blue
            case DESSERT -> new Color(218, 112, 214);   // Orchid
        };
    }

    // --- Action Listener and Helpers ---
    
    /**
     * Action listener for the MenuItem buttons.
     */
    private class AddItemToOrderAction implements ActionListener {
        private final MenuItem item;

        public AddItemToOrderAction(MenuItem item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Add the item to the order list model
            orderListModel.addElement(new OrderEntry(item.getName(), item.getPrice()));
            // Recalculate and update the total price
            updateTotal();
        }
    }
    
    /**
     * Recalculates the order total from the list model and updates the total label.
     */
    private void updateTotal() {
        BigDecimal currentTotal = BigDecimal.ZERO;
        for (int i = 0; i < orderListModel.getSize(); i++) {
            OrderEntry entry = orderListModel.getElementAt(i);
            currentTotal = currentTotal.add(entry.price());
        }
        totalLabel.setText("$" + currentTotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }


    // --- Data Mocking ---
    

    // --- Demo Main Method ---

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Concessions POS System Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Set a preferred size for the 3-pane layout
            frame.setPreferredSize(new Dimension(1000, 600)); 
            
            OrderModel orderModel = new OrderModel();
            
            // Add the main panel to the frame
            OrderPanel orderPanel = new OrderPanel(orderModel);
            frame.setContentPane(orderPanel);
            
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}
