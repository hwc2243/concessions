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

import com.concessions.client.model.User;
import com.concessions.client.model.Organization;
import com.concessions.client.model.Location;
import com.concessions.client.model.Organization;

@MappedSuperclass
public abstract class BaseUser<T extends BaseUser> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  protected Long id = null;

  @Column
  protected String username = null;
  
  @Column
  protected String firstName = null;
  
  @Column
  protected String lastName = null;
  
  @Column
  protected String authProvider = null;
  

  @ManyToOne
  @JoinColumn(name= "organizationId", nullable=true)
  protected Organization organization;

  @ManyToMany(cascade = {CascadeType.MERGE})
  @JoinTable(name = "user_location",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "location_id"))
  protected List<Location> locations;

  @ManyToMany(cascade = {CascadeType.MERGE})
  @JoinTable(name = "user_organization",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "organization_id"))
  protected List<Organization> organizations;


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

  public String getUsername ()
  {
    return this.username;
  }
  
  public void setUsername (String username)
  {
    this.username = username;
  }

  public String getFirstName ()
  {
    return this.firstName;
  }
  
  public void setFirstName (String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName ()
  {
    return this.lastName;
  }
  
  public void setLastName (String lastName)
  {
    this.lastName = lastName;
  }

  public String getAuthProvider ()
  {
    return this.authProvider;
  }
  
  public void setAuthProvider (String authProvider)
  {
    this.authProvider = authProvider;
  }

  public Organization getOrganization ()
  {
    return (Organization)this.organization;
  }
  
  public void setOrganization (Organization organization)
  {
    this.organization = organization;
  }


  public List<Location> getLocations ()
  {
    return this.locations;
  }
  
  public void setLocations (List<Location> locations)
  {
    this.locations = locations;
  }


  public List<Organization> getOrganizations ()
  {
    return this.organizations;
  }
  
  public void setOrganizations (List<Organization> organizations)
  {
    this.organizations = organizations;
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
			
		BaseUser other = (BaseUser) obj;
		return id == other.id;
	}

}