package com.concessions.dto;

import java.math.BigDecimal;

import java.util.List;

public class OrderItemDTO
{
  protected String id;

  protected Long menuItemId = null;
  
  protected String name = null;
  
  protected BigDecimal price = null;
  

  public OrderItemDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private OrderItemDTO (Builder builder)
  {
    this.id = builder.id;
    this.menuItemId = builder.menuItemId;
    this.name = builder.name;
    this.price = builder.price;
  }

  public String getId ()
  {
    return this.id;
  }
  
  public void setId (String id)
  {
    this.id = id;
  }


  public Long getMenuItemId ()
  {
    return this.menuItemId;
  }
  
  public void setMenuItemId (Long menuItemId)
  {
    this.menuItemId = menuItemId;
  }
  
  public String getName ()
  {
    return this.name;
  }
  
  public void setName (String name)
  {
    this.name = name;
  }
  
  public BigDecimal getPrice ()
  {
    return this.price;
  }
  
  public void setPrice (BigDecimal price)
  {
    this.price = price;
  }
  
  public static class Builder {

	private String id;
    private Long menuItemId = null;
    private String name = null;
    private BigDecimal price = null;

    public Builder id(String id) {
      this.id = id;
      return this;
    }
    
    public Builder menuItemId(Long menuItemId) {
      this.menuItemId = menuItemId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    /**
     * The build method creates and returns the immutable Entity object.
     */
    public OrderItemDTO build() {
      return new OrderItemDTO(this);
    }
  }
}