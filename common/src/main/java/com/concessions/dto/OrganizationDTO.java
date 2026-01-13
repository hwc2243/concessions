package com.concessions.dto;


import java.util.List;
import java.util.Objects;

public class OrganizationDTO
{
  protected Long id;

  protected String name = null;
  
  protected AddressDTO address;


  public OrganizationDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private OrganizationDTO (Builder builder)
  {
    this.id = builder.id;
    this.name = builder.name;
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
			
		OrganizationDTO other = (OrganizationDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private String name = null;
    private AddressDTO address = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder address(AddressDTO address) {
      this.address = address;
      return this;
    }
    /**
     * The build method creates and returns the immutable Entity object.
     */
    public OrganizationDTO build() {
      return new OrganizationDTO(this);
    }
  }
}