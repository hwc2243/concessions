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

import com.concessions.client.model.Location;
import com.concessions.client.model.Address;

import com.concessions.client.model.base.Multitenant;

@MappedSuperclass
public abstract class BaseLocation<T extends BaseLocation> extends AbstractBaseEntity
    implements Multitenant, Serializable
{
  @Id
  @Column
  protected Long id = null;

  @Column
  protected String name = null;
  
  @Column
  protected Long organizationId = null;
  

  @OneToOne(cascade=CascadeType.ALL)
  @JoinColumn(name= "address_id", nullable=true)
  protected Address address;
  

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

  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
  }

  public Address getAddress ()
  {
    return (Address)this.address;
  }
  
  public void setAddress (Address address)
  {
    this.address = address;
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
			
		BaseLocation other = (BaseLocation) obj;
		return id == other.id;
	}

}