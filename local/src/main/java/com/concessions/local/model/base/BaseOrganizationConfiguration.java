package com.concessions.local.model.base;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.io.Serializable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.concessions.local.model.OrganizationConfiguration;

@MappedSuperclass
public abstract class BaseOrganizationConfiguration<T extends BaseOrganizationConfiguration> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id = null;

  @Column
  protected Long organizationId = null;
  
  @Column
  protected String organizationName = null;
  
  @Column
  protected Long locationId = null;
  
  @Column
  protected String locationName = null;
  
  @Column
  protected Long menuId = null;
  
  @Column
  protected String menuName = null;
  


  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
  }
  
  public Object getKey ()
  {
    return this.id;
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
			
		BaseOrganizationConfiguration other = (BaseOrganizationConfiguration) obj;
		return id == other.id;
	}

}