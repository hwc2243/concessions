package com.concessions.client.model;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.client.model.base.BaseAddress;

@Entity
@Table(name="address")
public class Address extends BaseAddress<Address>
    implements Serializable
{
	public Address ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private Address(Builder builder)
    {
        this.street1 = builder.street1;
        this.street2 = builder.street2;
        this.city = builder.city;
        this.state = builder.state;
        this.zipCode = builder.zipCode;
        this.country = builder.country;
    }

    public static class Builder {

        private String street1 = null;
        private String street2 = null;
        private String city = null;
        private String state = null;
        private String zipCode = null;
        private String country = null;

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
        public Address build() {
            return new Address(this);
        }
    }
}