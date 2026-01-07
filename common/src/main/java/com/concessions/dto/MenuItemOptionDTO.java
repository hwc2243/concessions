package com.concessions.dto;

import java.math.BigDecimal;

import java.util.List;

public class MenuItemOptionDTO
{
  protected Long id;

  protected String name = null;
  
  protected String description = null;
  
  protected BigDecimal price = null;
  
  protected Long organizationId = null;
  

  public MenuItemOptionDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private MenuItemOptionDTO (Builder builder)
  {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.price = builder.price;
    this.organizationId = builder.organizationId;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
  }


  public String getName ()
  {
    return this.name;
  }
  
  public void setName (String name)
  {
    this.name = name;
  }
  
  public String getDescription ()
  {
    return this.description;
  }
  
  public void setDescription (String description)
  {
    this.description = description;
  }
  
  public BigDecimal getPrice ()
  {
    return this.price;
  }
  
  public void setPrice (BigDecimal price)
  {
    this.price = price;
  }
  
  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
  }
  
  public static class Builder {

	private Long id;
    private String name = null;
    private String description = null;
    private BigDecimal price = null;
    private Long organizationId = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    public Builder organizationId(Long organizationId) {
      this.organizationId = organizationId;
      return this;
    }

    /**
     * The build method creates and returns the immutable Entity object.
     */
    public MenuItemOptionDTO build() {
      return new MenuItemOptionDTO(this);
    }
  }
}