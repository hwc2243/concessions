package com.concessions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import java.util.List;

public class OrderDTO
{
  protected String id;

  protected String journalId = null;
  
  protected Long menuId = null;
  
  protected BigDecimal orderTotal = null;
  
  protected LocalDateTime startTs = null;
  
  protected LocalDateTime endTs = null;
  
  protected List<OrderItemDTO> orderItems;


  public OrderDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private OrderDTO (Builder builder)
  {
    this.id = builder.id;
    this.journalId = builder.journalId;
    this.menuId = builder.menuId;
    this.orderTotal = builder.orderTotal;
    this.startTs = builder.startTs;
    this.endTs = builder.endTs;
    this.orderItems = builder.orderItems;
  }

  public String getId ()
  {
    return this.id;
  }
  
  public void setId (String id)
  {
    this.id = id;
  }


  public String getJournalId ()
  {
    return this.journalId;
  }
  
  public void setJournalId (String journalId)
  {
    this.journalId = journalId;
  }
  
  public Long getMenuId ()
  {
    return this.menuId;
  }
  
  public void setMenuId (Long menuId)
  {
    this.menuId = menuId;
  }
  
  public BigDecimal getOrderTotal ()
  {
    return this.orderTotal;
  }
  
  public void setOrderTotal (BigDecimal orderTotal)
  {
    this.orderTotal = orderTotal;
  }
  
  public LocalDateTime getStartTs ()
  {
    return this.startTs;
  }
  
  public void setStartTs (LocalDateTime startTs)
  {
    this.startTs = startTs;
  }
  
  public LocalDateTime getEndTs ()
  {
    return this.endTs;
  }
  
  public void setEndTs (LocalDateTime endTs)
  {
    this.endTs = endTs;
  }
  
  public List<OrderItemDTO> getOrderItems ()
  {
    return this.orderItems;
  }
  
  public void setOrderItems (List<OrderItemDTO> orderItems)
  {
    this.orderItems = orderItems;
  }

  public static class Builder {

	private String id;
    private String journalId = null;
    private Long menuId = null;
    private BigDecimal orderTotal = null;
    private LocalDateTime startTs = null;
    private LocalDateTime endTs = null;
    private List<OrderItemDTO> orderItems = null;

    public Builder id(String id) {
      this.id = id;
      return this;
    }
    
    public Builder journalId(String journalId) {
      this.journalId = journalId;
      return this;
    }

    public Builder menuId(Long menuId) {
      this.menuId = menuId;
      return this;
    }

    public Builder orderTotal(BigDecimal orderTotal) {
      this.orderTotal = orderTotal;
      return this;
    }

    public Builder startTs(LocalDateTime startTs) {
      this.startTs = startTs;
      return this;
    }

    public Builder endTs(LocalDateTime endTs) {
      this.endTs = endTs;
      return this;
    }

    public Builder orderItems(List<OrderItemDTO> orderItems) {
      this.orderItems = orderItems;
      return this;
    }
    /**
     * The build method creates and returns the immutable Entity object.
     */
    public OrderDTO build() {
      return new OrderDTO(this);
    }
  }
}