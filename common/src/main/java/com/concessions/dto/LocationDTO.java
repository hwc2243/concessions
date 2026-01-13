package com.concessions.dto;


import java.util.List;
import java.util.Objects;

public class LocationDTO
{
  protected Long id;

  protected String name = null;
  
  protected Long organizationId = null;
  
  protected AddressDTO address;


  public LocationDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private LocationDTO (Builder builder)
  {
    this.id = builder.id;
    this.name = builder.name;
    this.organizationId = builder.organizationId;
    this.address = builder.address;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
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
  
  public AddressDTO getAddress ()
  {
    return (AddressDTO)this.address;
  }
  
  public void setAddress (AddressDTO address)
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
			
		LocationDTO other = (LocationDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private String name = null;
    private Long organizationId = null;
    private AddressDTO address = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder organizationId(Long organizationId) {
      this.organizationId = organizationId;
      return this;
    }

    public Builder address(AddressDTO address) {
      this.address = address;
      return this;
    }
    /**
     * The build method creates and returns the immutable Entity object.
     */
    public LocationDTO build() {
      return new LocationDTO(this);
    }
  }
}