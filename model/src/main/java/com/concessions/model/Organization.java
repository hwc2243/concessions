package com.concessions.model;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseOrganization;

@Entity
@Table(name="organization")
public class Organization extends BaseOrganization<Organization>
    implements Comparable, Serializable
{
	public Organization ()
	{
		super();
	}
	
	public String toString () {
		return this.getName();
	}
    // Private constructor to force the use of the Builder
    private Organization(Builder builder)
    {
        this.name = builder.name;
    }

    public static class Builder {

        private String name = null;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public Organization build() {
            return new Organization(this);
        }
    }

	@Override
	public int compareTo(Object o) {
		if (o instanceof Organization) {
			Organization org = (Organization)o;
			return this.getName().compareTo(org.getName());
		}
		return 0;
	}
}