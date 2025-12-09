package com.concessions.model.base;

import java.math.BigDecimal;
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
import java.util.UUID;

import com.concessions.model.OrderItem;

@MappedSuperclass
public abstract class BaseOrderItem<T extends BaseOrderItem> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  protected String id = null;

  @Column
  protected Long menuItemId = null;
  
  @Column
  protected String name = null;
  
  @Column(precision = 10, scale = 2)
  protected BigDecimal price = null;
  

  public BaseOrderItem ()
  {
    if (id == null || "".equals(id)) {
      id = UUID.randomUUID().toString();
    }
  }
  
  public String getId ()
  {
    return this.id;
  }
  
  public void setId (String id)
  {
    this.id = id;
  }
  
  public Object getKey ()
  {
    return this.id;
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
			
		BaseOrderItem other = (BaseOrderItem) obj;
		return this.getId().equals(other.getId());
	}

}