package com.concessions.dto;


import java.util.List;

public class MenuDTO
{
  protected Long id;

  protected String name = null;
  
  protected String description = null;
  
  protected Long organizationId = null;
  
  protected List<MenuItemDTO> menuItems;


  public MenuDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private MenuDTO (Builder builder)
  {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.organizationId = builder.organizationId;
    this.menuItems = builder.menuItems;
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
  
  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
  }
  
  public List<MenuItemDTO> getMenuItems ()
  {
    return this.menuItems;
  }
  
  public void setMenuItems (List<MenuItemDTO> menuItems)
  {
    this.menuItems = menuItems;
  }

  public static class Builder {

	private Long id;
    private String name = null;
    private String description = null;
    private Long organizationId = null;
    private List<MenuItemDTO> menuItems = null;

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

    public Builder organizationId(Long organizationId) {
      this.organizationId = organizationId;
      return this;
    }

    public Builder menuItems(List<MenuItemDTO> menuItems) {
      this.menuItems = menuItems;
      return this;
    }
    /**
     * The build method creates and returns the immutable Entity object.
     */
    public MenuDTO build() {
      return new MenuDTO(this);
    }
  }
}