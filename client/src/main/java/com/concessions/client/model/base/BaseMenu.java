package com.concessions.client.model.base;

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

import com.concessions.client.model.Menu;
import com.concessions.client.model.MenuItem;

import com.concessions.client.model.base.Multitenant;

@MappedSuperclass
public abstract class BaseMenu<T extends BaseMenu> extends AbstractBaseEntity
    implements Multitenant, Serializable
{
  @Id
  @Column
  protected Long id = null;

  @Column
  protected String name = null;
  
  @Column
  protected String description = null;
  
  @Column
  protected Long organizationId = null;
  

  @ManyToMany(cascade = {CascadeType.MERGE})
  @JoinTable(name = "menu_menuItem",
             joinColumns = @JoinColumn(name = "menu_id"),
             inverseJoinColumns = @JoinColumn(name = "menuItem_id"))
  protected List<MenuItem> menuItems;


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

  public List<MenuItem> getMenuItems ()
  {
    return this.menuItems;
  }
  
  public void setMenuItems (List<MenuItem> menuItems)
  {
    this.menuItems = menuItems;
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
			
		BaseMenu other = (BaseMenu) obj;
		return id == other.id;
	}

}