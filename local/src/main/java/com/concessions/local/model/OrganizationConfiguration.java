package com.concessions.local.model;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.local.model.base.BaseOrganizationConfiguration;

@Entity
@Table(name="organizationConfiguration")
public class OrganizationConfiguration extends BaseOrganizationConfiguration<OrganizationConfiguration>
    implements Serializable
{
	public OrganizationConfiguration ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private OrganizationConfiguration(Builder builder)
    {
        this.organizationId = builder.organizationId;
        this.organizationName = builder.organizationName;
        this.locationId = builder.locationId;
        this.locationName = builder.locationName;
        this.menuId = builder.menuId;
        this.menuName = builder.menuName;
    }

    public static class Builder {

        private Long organizationId = null;
        private String organizationName = null;
        private Long locationId = null;
        private String locationName = null;
        private Long menuId = null;
        private String menuName = null;

        public Builder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public Builder locationId(Long locationId) {
            this.locationId = locationId;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder menuId(Long menuId) {
            this.menuId = menuId;
            return this;
        }

        public Builder menuName(String menuName) {
            this.menuName = menuName;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public OrganizationConfiguration build() {
            return new OrganizationConfiguration(this);
        }
    }
}