package com.concessions.local.dto;


import java.util.List;
import java.util.Objects;

public class LocationConfigurationDTO
{
  protected Long id;

  protected Long organizationId = null;
  
  protected String organizationName = null;
  
  protected Long locationId = null;
  
  protected String locationName = null;
  
  protected Long menuId = null;
  
  protected String menuName = null;
  
  protected Integer pin = null;
  

  public LocationConfigurationDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private LocationConfigurationDTO (Builder builder)
  {
    this.id = builder.id;
    this.organizationId = builder.organizationId;
    this.organizationName = builder.organizationName;
    this.locationId = builder.locationId;
    this.locationName = builder.locationName;
    this.menuId = builder.menuId;
    this.menuName = builder.menuName;
    this.pin = builder.pin;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
  }


  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
  }
  
  public String getOrganizationName ()
  {
    return this.organizationName;
  }
  
  public void setOrganizationName (String organizationName)
  {
    this.organizationName = organizationName;
  }
  
  public Long getLocationId ()
  {
    return this.locationId;
  }
  
  public void setLocationId (Long locationId)
  {
    this.locationId = locationId;
  }
  
  public String getLocationName ()
  {
    return this.locationName;
  }
  
  public void setLocationName (String locationName)
  {
    this.locationName = locationName;
  }
  
  public Long getMenuId ()
  {
    return this.menuId;
  }
  
  public void setMenuId (Long menuId)
  {
    this.menuId = menuId;
  }
  
  public String getMenuName ()
  {
    return this.menuName;
  }
  
  public void setMenuName (String menuName)
  {
    this.menuName = menuName;
  }
  
  public Integer getPin ()
  {
    return this.pin;
  }
  
  public void setPin (Integer pin)
  {
    this.pin = pin;
  }
  

    @Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
			
		LocationConfigurationDTO other = (LocationConfigurationDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private Long organizationId = null;
    private String organizationName = null;
    private Long locationId = null;
    private String locationName = null;
    private Long menuId = null;
    private String menuName = null;
    private Integer pin = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder organizationId(Long organizationId) {
      this.organizationId = organizationId;
      return this;
    }

    public Builder organizationName(String organizationName) {
      this.organizationName = organizationName;
      return this;
    }

    public Builder locationId(Long locationId) {
      this.locationId = locationId;
      return this;
    }

    public Builder locationName(String locationName) {
      this.locationName = locationName;
      return this;
    }

    public Builder menuId(Long menuId) {
      this.menuId = menuId;
      return this;
    }

    public Builder menuName(String menuName) {
      this.menuName = menuName;
      return this;
    }

    public Builder pin(Integer pin) {
      this.pin = pin;
      return this;
    }

    /**
     * The build method creates and returns the immutable Entity object.
     */
    public LocationConfigurationDTO build() {
      return new LocationConfigurationDTO(this);
    }
  }
}