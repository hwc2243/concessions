package com.concessions.model;

import com.concessions.model.CategoryType;
import java.math.BigDecimal;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseMenuItem;

@Entity
@Table(name="menuItem")
public class MenuItem extends BaseMenuItem<MenuItem>
    implements Serializable
{
	public MenuItem ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private MenuItem(Builder builder)
    {
        this.organizationId = builder.organizationId;
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.price = builder.price;
    }

    public static class Builder {

        private Long organizationId = null;
        private String name = null;
        private String description = null;
        private CategoryType category = null;
        private BigDecimal price = null;

        public Builder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(CategoryType category) {
            this.category = category;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public MenuItem build() {
            return new MenuItem(this);
        }
    }
}