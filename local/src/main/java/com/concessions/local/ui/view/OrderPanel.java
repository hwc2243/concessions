package com.concessions.local.ui.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import com.concessions.dto.CategoryType;
import com.concessions.dto.MenuItemDTO;
import com.concessions.local.ui.DisabledLayerUI;
import com.concessions.local.ui.model.OrderModel;
import com.concessions.local.ui.model.OrderModel.OrderEntry;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A three-pane UI component for ordering in a concessions application.
 * 1. Left: Category buttons
 * 2. Center: Menu Item buttons (dynamic based on selected category)
 * 3. Right: Current Order list
 */
public class OrderPanel extends JPanel implements PropertyChangeListener {

	public static final String NAME = "ORDER";
	
    private OrderModel orderModel;
    
    // --- UI Components ---
    private final JPanel categoryButtonPanel = new JPanel();
    private final JPanel itemCardPanel = new JPanel(new CardLayout()); // Uses CardLayout to swap item panels
    private final JLabel totalLabel = new JLabel("$0.00");
    private final JPanel centerTitlePanel = new JPanel(new BorderLayout(5, 5));
    
    private JPanel contentPane;
	private DisabledLayerUI disableLayerUI;
	private JLayer<JPanel> disableLayer;

    private List<OrderActionListener> listeners = new ArrayList<>();
    
    public OrderPanel (OrderModel orderModel) {
    	this.orderModel = orderModel;
    	orderModel.addPropertyChangeListener(this);
    	
    	initializeUI();
    	
    	updateMenuUI(orderModel.getMenuData());
    }
    
    private void initializeUI() {
        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.weighty = 1.0;

        // --- 1. Left Pane ---
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        categoryButtonPanel.setLayout(new GridLayout(0, 1, 5, 5));
        categoryButtonPanel.setBackground(Color.WHITE);
        
        JScrollPane leftScroll = new JScrollPane(categoryButtonPanel);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Categories"));
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(leftScroll, gbc);

        // --- 2. Center Pane ---
        gbc.gridx = 1;
        gbc.weightx = 2.5;
        
        JScrollPane centerScroll = new JScrollPane(itemCardPanel);
        centerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerTitlePanel.add(centerScroll, BorderLayout.CENTER);
        contentPane.add(centerTitlePanel, gbc);

        // --- 3. Right Pane ---
        gbc.gridx = 2;
        gbc.weightx = 1.5;
        contentPane.add(createRightPane(), gbc);

        // Layering for Disabled State
        disableLayerUI = new DisabledLayerUI();
        disableLayer = new JLayer<>(contentPane, disableLayerUI);
        
        setLayout(new BorderLayout());
        add(disableLayer, BorderLayout.CENTER);
    }
    
    private void updateMenuUI(Map<CategoryType, List<MenuItemDTO>> menuData) {
        // Clear existing components
        categoryButtonPanel.removeAll();
        itemCardPanel.removeAll();

        if (menuData == null || menuData.isEmpty()) {
            revalidate();
            repaint();
            return;
        }

        Set<CategoryType> sortedCategories = new TreeSet<>(menuData.keySet());

        for (CategoryType category : sortedCategories) {
            // A. Create Category Button (Left)
            JButton catButton = new JButton(category.getName());
            catButton.setBackground(getCategoryColor(category));
            catButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            catButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) itemCardPanel.getLayout();
                cl.show(itemCardPanel, category.getName());
                centerTitlePanel.setBorder(BorderFactory.createTitledBorder(category.getName()));
            });
            categoryButtonPanel.add(catButton);

            // B. Create Item Panel (Center)
            JPanel categoryItemPanel = new JPanel(new GridLayout(0, 2, 8, 8));
            categoryItemPanel.setBackground(Color.WHITE);
            
            for (MenuItemDTO item : menuData.get(category)) {
                JButton itemButton = createItemButton(item);
                categoryItemPanel.add(itemButton);
            }

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.add(categoryItemPanel, BorderLayout.NORTH);
            itemCardPanel.add(wrapper, category.getName());
        }

        // Default to first category
        CategoryType first = sortedCategories.iterator().next();
        ((CardLayout) itemCardPanel.getLayout()).show(itemCardPanel, first.getName());
        centerTitlePanel.setBorder(BorderFactory.createTitledBorder(first.getName()));

        revalidate();
        repaint();
    }
    
    private JButton createItemButton(MenuItemDTO item) {
        String label = String.format("<html><center>%s<br><font color='#555555'>$%s</font></center></html>", 
                                     item.getName(), item.getPrice().setScale(2, RoundingMode.HALF_UP));
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.addActionListener(new AddItemToOrderAction(item));
        return btn;
    }
    
    public void setInteractiveState (boolean enabled) {
		setInteractiveState(enabled, null);
	}
    
    public void setInteractiveState (boolean enabled, String message) {
		if (enabled) {
			disableLayer.setEnabled(true);
		} else {
			if (StringUtils.isEmpty(message)) {
				message = "Disabled";
			}
			disableLayerUI.setMessage(message);
			disableLayer.setEnabled(false);
		}
		
		disableLayer.repaint();
		
		super.setEnabled(enabled);
	}
	
    public void addOrderActionListener (OrderActionListener listener) {
    	listeners.add(listener);
    }
    
    public void removeOrderActionListener (OrderActionListener listener) {
    	listeners.remove(listener);
    }
    
    // --- Pane Initialization Methods ---
    private JScrollPane createLeftPane (Set<CategoryType> categories) {
    	JPanel categoryPanel = new JPanel();
    	categoryPanel.setLayout(new GridLayout(0, 1, 5, 5));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (CategoryType category : categories) {
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Categories"));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        return scrollPane;
    }

    private JPanel createCenterPane(Map<CategoryType, List<MenuItemDTO>> menuData) {
        
        // Populate the CardLayout with specific panels for each category
        for (Map.Entry<CategoryType, List<MenuItemDTO>> entry : menuData.entrySet()) {
            CategoryType category = entry.getKey();
            List<MenuItemDTO> items = entry.getValue();
            
            // Create a panel for the items of this category
            JPanel categoryItemPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 columns for items
            
            for (MenuItemDTO item : items) {
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

        // Order List
        JList<OrderEntry> orderList = new JList<>(orderModel);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // Make sure set preferred size big enough
        FontMetrics fm = orderList.getFontMetrics(orderList.getFont());
        int fixedListWidth = fm.stringWidth("Item Name Max Width (25 chars) $99.99") + 20;
        
        JScrollPane scrollPane = new JScrollPane(orderList);
        Dimension scrollPaneSize = scrollPane.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(fixedListWidth, scrollPaneSize.height));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Remove Item Button ---
        JButton removeButton = new JButton("Remove Selected Item");
        removeButton.setBackground(new Color(255, 150, 150)); // Light Red/Pink
        removeButton.setForeground(Color.BLACK);
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        removeButton.addActionListener(e -> {
            int selectedIndex = orderList.getSelectedIndex();
            if (selectedIndex != -1) {
            	notifyOnItemRemoved(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Please select an item from the list to remove.", 
                        "No Item Selected", 
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Panel to hold the List and the Remove Button
        JPanel listAndRemovePanel = new JPanel(new BorderLayout());
        listAndRemovePanel.add(scrollPane, BorderLayout.CENTER);
        listAndRemovePanel.add(removeButton, BorderLayout.SOUTH);
        
        // Footer (Total & Checkout)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalPanel.add(new JLabel("Total: "));
        totalPanel.add(totalLabel);
        
        // Clear Button
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(220, 20, 60)); // Crimson Red
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 18));
        clearButton.addActionListener(e -> {
            if (orderModel.getSize() > 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to clear the entire order?",
                        "Confirm Clear",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                	notifyOnClear();
                }
            }
        });
        
        // Checkout Button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutButton.addActionListener(e -> {
        	notifyOnCheckout();
        });        
        
        // Panel to hold both buttons, using GridLayout for equal sizing
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // 1 row, 2 columns, 5px horizontal gap
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        
        footerPanel.add(totalPanel, BorderLayout.NORTH);
        footerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        orderPane.add(listAndRemovePanel, BorderLayout.CENTER);
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
        private final MenuItemDTO item;

        public AddItemToOrderAction(MenuItemDTO item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	notifyOnItemAdded(item);
        }
    }
    
    protected void notifyOnItemAdded (MenuItemDTO item) {
    	listeners.stream().forEach(listener -> listener.onItemAdded(item));
    }

    protected void notifyOnItemRemoved (int index) {
    	listeners.stream().forEach(listener -> listener.onItemRemoved(index));
    }
    
    protected void notifyOnCheckout () {
    	listeners.stream().forEach(OrderActionListener::onCheckout);
    }
    
    protected void notifyOnClear () {
    	listeners.stream().forEach(OrderActionListener::onClear);
    }
    
    public interface OrderActionListener {
    	
    	void onItemAdded (MenuItemDTO item);
    	
    	void onItemRemoved (int index);
    	
        /**
         * Called when the user initiates a checkout process.
         * @param total The final total amount of the order to process.
         */
        void onCheckout ();

        /**
         * Called when the user explicitly clears the current order.
         */
        void onClear ();
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
        case OrderModel.ORDER_TOTAL -> {
            BigDecimal total = (BigDecimal) evt.getNewValue();
            totalLabel.setText("$" + total.setScale(2, RoundingMode.HALF_UP).toString());
        }
        case OrderModel.MENU_DATA -> {
            updateMenuUI((Map<CategoryType, List<MenuItemDTO>>) evt.getNewValue());
        }
    }
	}
}
