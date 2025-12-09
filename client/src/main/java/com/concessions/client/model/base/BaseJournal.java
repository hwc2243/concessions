package com.concessions.client.model.base;

import com.concessions.client.model.StatusType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
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

import com.concessions.client.model.Journal;

import com.concessions.client.model.base.Multitenant;

@MappedSuperclass
public abstract class BaseJournal<T extends BaseJournal> extends AbstractBaseEntity
    implements Multitenant, Serializable
{
  @Id
  @Column
  protected String id = null;

  @Column
  @Enumerated(EnumType.STRING)
  protected StatusType status = null;
  
  @Column
  protected Long menuId = null;
  
  @Column
  protected Long orderCount = null;
  
  @Column(precision = 10, scale = 2)
  protected BigDecimal salesTotal = null;
  
  @Column
  protected LocalDateTime startTs = null;
  
  @Column
  protected LocalDateTime endTs = null;
  
  @Column
  protected LocalDateTime syncTs = null;
  
  @Column
  protected Long organizationId = null;
  

  public BaseJournal ()
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

  public StatusType getStatus ()
  {
    return this.status;
  }
  
  public void setStatus (StatusType status)
  {
    this.status = status;
  }

  public Long getMenuId ()
  {
    return this.menuId;
  }
  
  public void setMenuId (Long menuId)
  {
    this.menuId = menuId;
  }

  public Long getOrderCount ()
  {
    return this.orderCount;
  }
  
  public void setOrderCount (Long orderCount)
  {
    this.orderCount = orderCount;
  }

  public BigDecimal getSalesTotal ()
  {
    return this.salesTotal;
  }
  
  public void setSalesTotal (BigDecimal salesTotal)
  {
    this.salesTotal = salesTotal;
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

  public LocalDateTime getSyncTs ()
  {
    return this.syncTs;
  }
  
  public void setSyncTs (LocalDateTime syncTs)
  {
    this.syncTs = syncTs;
  }

  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
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
			
		BaseJournal other = (BaseJournal) obj;
		return this.getId().equals(other.getId());
	}

}