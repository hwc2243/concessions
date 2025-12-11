package com.concessions.local.model;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.local.model.base.BaseDevice;

@Entity
@Table(name="device")
public class Device extends BaseDevice<Device>
    implements Serializable
{
	public Device ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private Device(Builder builder)
    {
        this.deviceId = builder.deviceId;
    }

    public static class Builder {

        private String deviceId = null;

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public Device build() {
            return new Device(this);
        }
    }
}