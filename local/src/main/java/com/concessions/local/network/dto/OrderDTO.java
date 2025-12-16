package com.concessions.local.network.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDTO implements Serializable {

    private String id;
    private String journalId;
    private Long menuId;
    private BigDecimal orderTotal;
    private LocalDateTime startTs;
    private LocalDateTime endTs;
    
    // Relationship mapped to its DTO counterpart
    private List<OrderItemDTO> orderItems;

	public OrderDTO() {
		// TODO Auto-generated constructor stub
	}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public LocalDateTime getStartTs() {
        return startTs;
    }

    public void setStartTs(LocalDateTime startTs) {
        this.startTs = startTs;
    }

    public LocalDateTime getEndTs() {
        return endTs;
    }

    public void setEndTs(LocalDateTime endTs) {
        this.endTs = endTs;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDTO that = (OrderDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    public void addOrderItem (OrderItemDTO orderItem) {
    	if (orderItems == null) {
    		orderItems = new ArrayList<>();
    	}
    	orderItems.add(orderItem);
    }
}
