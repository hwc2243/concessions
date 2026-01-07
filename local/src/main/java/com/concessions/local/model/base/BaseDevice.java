package com.concessions.local.model.base;

import com.concessions.local.model.DeviceTypeType;
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

import com.concessions.local.model.Device;

@MappedSuperclass
public abstract class BaseDevice<T extends BaseDevice> extends AbstractBaseEntity
    implements Serializable
{
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id = null;

  @Column
  protected String deviceId = null;
  
  @Column
  @Enumerated(EnumType.STRING)
  protected DeviceTypeType deviceType = null;
  
  @Column
  protected String deviceIp = null;
  
  @Column
  protected Integer devicePort = null;
  

  
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
			
		BaseDevice other = (BaseDevice) obj;
		return id == other.id;
	}

}