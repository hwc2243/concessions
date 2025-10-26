package com.concessions.local.ui.model;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.concessions.model.CategoryType;
import com.concessions.model.MenuItem;

public class OrderModel extends AbstractModel {

    private Map<CategoryType, List<MenuItem>> menuData = new LinkedHashMap<>();

	public OrderModel() {
	}

	public Map<CategoryType, List<MenuItem>> getMenuData ()
	{
		return this.menuData;
	}
	
	public void setMenuData (Map<CategoryType, List<MenuItem>> menuData)
	{
		Map<CategoryType, List<MenuItem>> oldMenuData = this.menuData;
		this.menuData = menuData;
		this.firePropertyChange("MENU_DATA", menuData, oldMenuData);
	}
	
	/*
    private void initializeMockData() {
        Category entree = new Category("C1", "Entrees", new Color(255, 99, 71)); // Tomato
        Category sides = new Category("C2", "Sides", new Color(255, 165, 0)); // Orange
        Category drinks = new Category("C3", "Drinks", new Color(70, 130, 180)); // Steel Blue
        Category desserts = new Category("C4", "Desserts", new Color(180, 130, 70));
        
        menuData.put(entree, List.of(
            new MenuItem(UUID.randomUUID().toString(), "Classic Burger", new BigDecimal("8.50"), entree),
            new MenuItem(UUID.randomUUID().toString(), "Veggie Wrap", new BigDecimal("7.75"), entree),
            new MenuItem(UUID.randomUUID().toString(), "Hot Dog Special", new BigDecimal("6.00"), entree),
            new MenuItem(UUID.randomUUID().toString(), "Nachos Grande", new BigDecimal("10.99"), entree)
        ));
        
        menuData.put(sides, List.of(
            new MenuItem(UUID.randomUUID().toString(), "Fries (Lg)", new BigDecimal("4.00"), sides),
            new MenuItem(UUID.randomUUID().toString(), "Onion Rings", new BigDecimal("5.25"), sides),
            new MenuItem(UUID.randomUUID().toString(), "Pretzel", new BigDecimal("3.50"), sides),
            new MenuItem(UUID.randomUUID().toString(), "Side Salad", new BigDecimal("4.50"), sides)
        ));
        
        menuData.put(drinks, List.of(
            new MenuItem(UUID.randomUUID().toString(), "Soda (Reg)", new BigDecimal("2.50"), drinks),
            new MenuItem(UUID.randomUUID().toString(), "Water Bottle", new BigDecimal("2.00"), drinks),
            new MenuItem(UUID.randomUUID().toString(), "Lemonade", new BigDecimal("3.00"), drinks)
        ));
        
        menuData.put(desserts, List.of(
        		new MenuItem(UUID.randomUUID().toString(), "Ice Cream", new BigDecimal("3.50"), desserts)));
    }
    */


}
