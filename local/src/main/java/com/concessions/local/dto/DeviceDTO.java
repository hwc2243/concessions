package com.concessions.local.dto;

import com.concessions.local.dto.DeviceTypeType;

import java.util.List;
import java.util.Objects;

public class DeviceDTO
{
  protected Long id;

  protected String deviceId = null;
  
  protected DeviceTypeType deviceType = null;
  
  protected String deviceIp = null;
  
  protected Integer devicePort = null;
  

  public DeviceDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private DeviceDTO (Builder builder)
  {
    this.id = builder.id;
    this.deviceId = builder.deviceId;
    this.deviceType = builder.deviceType;
    this.deviceIp = builder.deviceIp;
    this.devicePort = builder.devicePort;
  }

  public Long getId ()
  {
    return this.id;
  }
  
  public void setId (Long id)
  {
    this.id = id;
  }


  public String getDeviceId ()
  {
    return this.deviceId;
  }
  
  public void setDeviceId (String deviceId)
  {
    this.deviceId = deviceId;
  }
  
  public DeviceTypeType getDeviceType ()
  {
    return this.deviceType;
  }
  
  public void setDeviceType (DeviceTypeType deviceType)
  {
    this.deviceType = deviceType;
  }
  
  public String getDeviceIp ()
  {
    return this.deviceIp;
  }
  
  public void setDeviceIp (String deviceIp)
  {
    this.deviceIp = deviceIp;
  }
  
  public Integer getDevicePort ()
  {
    return this.devicePort;
  }
  
  public void setDevicePort (Integer devicePort)
  {
    this.devicePort = devicePort;
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
			
		DeviceDTO other = (DeviceDTO) obj;
		return id == other.id;
	}

  public static class Builder {

	private Long id;
    private String deviceId = null;
    private DeviceTypeType deviceType = null;
    private String deviceIp = null;
    private Integer devicePort = null;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder deviceId(String deviceId) {
      this.deviceId = deviceId;
      return this;
    }

    public Builder deviceType(DeviceTypeType deviceType) {
      this.deviceType = deviceType;
      return this;
    }

    public Builder deviceIp(String deviceIp) {
      this.deviceIp = deviceIp;
      return this;
    }

    public Builder devicePort(Integer devicePort) {
      this.devicePort = devicePort;
      return this;
    }

    /**
     * The build method creates and returns the immutable Entity object.
     */
    public DeviceDTO build() {
      return new DeviceDTO(this);
    }
  }
}