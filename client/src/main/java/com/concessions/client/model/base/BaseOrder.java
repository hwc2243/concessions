package com.concessions.client.model.base;

import java.math.BigDecimal;
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

import com.concessions.client.model.Order;
import com.concessions.client.model.OrderItem;

@MappedSuperclass
public abstract class BaseOrder<T extends BaseOrder> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  protected String id = null;

  @Column
  protected String journalId = null;
  
  @Column
  protected Long menuId = null;
  
  @Column(precision = 10, scale = 2)
  protected BigDecimal orderTotal = null;
  
  @Column
  protected LocalDateTime startTs = null;
  
  @Column
  protected LocalDateTime endTs = null;
  

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "orderId")
  protected List<OrderItem> orderItems;
  
  public BaseOrder ()
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

  public List<OrderItem> getOrderItems ()
  {
    return this.orderItems;
  }
  
  public void setOrderItems (List<OrderItem> orderItems)
  {
    this.orderItems = orderItems;
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
			
		BaseOrder other = (BaseOrder) obj;
		return this.getId().equals(other.getId());
	}

}