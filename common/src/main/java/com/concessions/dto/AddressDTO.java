package com.concessions.dto;


import java.util.List;
import java.util.Objects;

public class AddressDTO
{
  protected Long id;

  protected String street1 = null;
  
  protected String street2 = null;
  
  protected String city = null;
  
  protected String state = null;
  
  protected String zipCode = null;
  
  protected String country = null;
  

  public AddressDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private AddressDTO (Builder builder)
  {
    this.id = builder.id;
    this.street1 = builder.street1;
    this.street2 = builder.street2;
    this.city = builder.city;
    this.state = builder.state;
    this.zipCode = builder.zipCode;
    this.country = builder.country;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
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
			
		AddressDTO other = (AddressDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private String street1 = null;
    private String street2 = null;
    private String city = null;
    private String state = null;
    private String zipCode = null;
    private String country = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder street1(String street1) {
      this.street1 = street1;
      return this;
    }

    public Builder street2(String street2) {
      this.street2 = street2;
      return this;
    }

    public Builder city(String city) {
      this.city = city;
      return this;
    }

    public Builder state(String state) {
      this.state = state;
      return this;
    }

    public Builder zipCode(String zipCode) {
      this.zipCode = zipCode;
      return this;
    }

    public Builder country(String country) {
      this.country = country;
      return this;
    }

    /**
     * The build method creates and returns the immutable Entity object.
     */
    public AddressDTO build() {
      return new AddressDTO(this);
    }
  }
}