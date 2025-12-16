package com.concessions.local.ui.model;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.AbstractListModel;

import com.concessions.client.model.CategoryType;
import com.concessions.local.network.dto.MenuDTO;
import com.concessions.local.network.dto.MenuItemDTO;

public class OrderModel extends AbstractListModel {
	
	public static final String MENU_DATA = "MENU_DATA";

	public static final String ORDER_TOTAL = "ORDER_TOTAL";
	
    public record OrderEntry(MenuItemDTO menuItem) {
        @Override
        public String toString() {
            return String.format("%-25s $%s", menuItem.getName(), menuItem.getPrice().setScale(2));
        }
    }
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private MenuDTO menu = null;
	
    private Map<CategoryType, List<MenuItemDTO>> menuData = new LinkedHashMap<>();
    
    protected List<OrderEntry> orderEntries = new ArrayList<>();

    protected BigDecimal orderTotal = BigDecimal.ZERO;
    
	public OrderModel () {
	}

	public MenuDTO getMenu () {
		return this.menu;
	}
	
	public void setMenu (MenuDTO menu) {
		Map<CategoryType, List<MenuItemDTO>> oldMenuData = this.menuData;
		this.menu = menu;
		this.menuData = menu.getMenuItems().stream()
	            .collect(Collectors.groupingBy(MenuItemDTO::getCategory));
		this.firePropertyChange(MENU_DATA, menuData, oldMenuData);
	}
	
	public Map<CategoryType, List<MenuItemDTO>> getMenuData ()
	{
		return this.menuData;
	}
	
	/*
	public void setMenuData (Map<CategoryType, List<MenuItem>> menuData)
	{
		Map<CategoryType, List<MenuItem>> oldMenuData = this.menuData;
		this.menuData = menuData;
	}
	*/

	
	public BigDecimal getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal (BigDecimal orderTotal) {
		BigDecimal oldOrderTotal = this.orderTotal;
		this.orderTotal = orderTotal;
		this.firePropertyChange(ORDER_TOTAL, oldOrderTotal, orderTotal);
	}

	// methods related to the ListModel
	public void add (OrderEntry orderEntry) {
		orderEntries.add(orderEntry);
		fireIntervalAdded(this, orderEntries.size() - 1, orderEntries.size() - 1);
	}
	
	public void clear () {
		int size = getSize();
		
		orderEntries.clear();
		fireIntervalRemoved(this, 0, size-1);
	}
	
	public OrderEntry get (int index) {
		return orderEntries.get(index);
	}
	
	@Override
	public Object getElementAt (int index) {
		return get(index);
	}
	
    public List<OrderEntry> getOrderEntries() {
    	return orderEntries;
    }

    @Override
	public int getSize() {
		return orderEntries.size();
	}

    public OrderEntry remove (int index) {
		OrderEntry orderEntry = orderEntries.remove(index);
		this.fireIntervalRemoved(this, index, index);
		return orderEntry;
	}
	


	// methods related to property change
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}
}
