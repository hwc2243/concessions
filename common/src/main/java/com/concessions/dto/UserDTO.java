package com.concessions.dto;


import java.util.List;
import java.util.Objects;

public class UserDTO
{
  protected Long id;

  protected String username = null;
  
  protected String firstName = null;
  
  protected String lastName = null;
  
  protected String authProvider = null;
  
  protected OrganizationDTO organization;

  protected List<LocationDTO> locations;

  protected List<OrganizationDTO> organizations;


  public UserDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private UserDTO (Builder builder)
  {
    this.id = builder.id;
    this.username = builder.username;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.authProvider = builder.authProvider;
    this.organization = builder.organization;
    this.locations = builder.locations;
    this.organizations = builder.organizations;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
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
  
  public OrganizationDTO getOrganization ()
  {
    return (OrganizationDTO)this.organization;
  }
  
  public void setOrganization (OrganizationDTO organization)
  {
    this.organization = organization;
  }

  public List<LocationDTO> getLocations ()
  {
    return this.locations;
  }
  
  public void setLocations (List<LocationDTO> locations)
  {
    this.locations = locations;
  }

  public List<OrganizationDTO> getOrganizations ()
  {
    return this.organizations;
  }
  
  public void setOrganizations (List<OrganizationDTO> organizations)
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
			
		UserDTO other = (UserDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private String username = null;
    private String firstName = null;
    private String lastName = null;
    private String authProvider = null;
    private OrganizationDTO organization = null;
    private List<LocationDTO> locations = null;
    private List<OrganizationDTO> organizations = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder authProvider(String authProvider) {
      this.authProvider = authProvider;
      return this;
    }

    public Builder organization(OrganizationDTO organization) {
      this.organization = organization;
      return this;
    }
    public Builder locations(List<LocationDTO> locations) {
      this.locations = locations;
      return this;
    }
    public Builder organizations(List<OrganizationDTO> organizations) {
      this.organizations = organizations;
      return this;
    }
    /**
     * The build method creates and returns the immutable Entity object.
     */
    public UserDTO build() {
      return new UserDTO(this);
    }
  }
}