package com.concessions.model;

import java.math.BigDecimal;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseOrderItem;

@Entity
@Table(name="orderItem")
public class OrderItem extends BaseOrderItem<OrderItem>
    implements Serializable
{
	public OrderItem ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private OrderItem(Builder builder)
    {
        this.menuItemId = builder.menuItemId;
        this.name = builder.name;
        this.price = builder.price;
    }

    public static class Builder {

        private Long menuItemId = null;
        private String name = null;
        private BigDecimal price = null;

        public Builder menuItemId(Long menuItemId) {
            this.menuItemId = menuItemId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}