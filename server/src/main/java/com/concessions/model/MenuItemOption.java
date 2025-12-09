package com.concessions.model;

import java.math.BigDecimal;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseMenuItemOption;

@Entity
@Table(name="menuItemOption")
public class MenuItemOption extends BaseMenuItemOption<MenuItemOption>
    implements Serializable
{
	public MenuItemOption ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private MenuItemOption(Builder builder)
    {
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.organizationId = builder.organizationId;
    }

    public static class Builder {

        private String name = null;
        private String description = null;
        private BigDecimal price = null;
        private Long organizationId = null;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public MenuItemOption build() {
            return new MenuItemOption(this);
        }
    }
}