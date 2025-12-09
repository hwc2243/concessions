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

import com.concessions.client.model.Address;

@MappedSuperclass
public abstract class BaseAddress<T extends BaseAddress> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  protected Long id = null;

  @Column(name="street1"
    )
  protected String street1 = null;
  
  @Column(name="street2"
    )
  protected String street2 = null;
  
  @Column(name="city"
    )
  protected String city = null;
  
  @Column(name="state"
    )
  protected String state = null;
  
  @Column(name="zip_code"
    )
  protected String zipCode = null;
  
  @Column(name="country"
    )
  protected String country = null;
  


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

  public String getStreet1 ()
  {
    return this.street1;
  }
  
  public void setStreet1 (String street1)
  {
    this.street1 = street1;
  }

  public String getStreet2 ()
  {
    return this.street2;
  }
  
  public void setStreet2 (String street2)
  {
    this.street2 = street2;
  }

  public String getCity ()
  {
    return this.city;
  }
  
  public void setCity (String city)
  {
    this.city = city;
  }

  public String getState ()
  {
    return this.state;
  }
  
  public void setState (String state)
  {
    this.state = state;
  }

  public String getZipCode ()
  {
    return this.zipCode;
  }
  
  public void setZipCode (String zipCode)
  {
    this.zipCode = zipCode;
  }

  public String getCountry ()
  {
    return this.country;
  }
  
  public void setCountry (String country)
  {
    this.country = country;
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
			
		BaseAddress other = (BaseAddress) obj;
		return id == other.id;
	}

}