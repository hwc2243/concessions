package com.concessions.client.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.client.model.base.BaseOrder;

@Entity
@Table(name="order_")
public class Order extends BaseOrder<Order>
    implements Serializable
{
	public Order ()
	{
		super();
		this.orderItems = new ArrayList<>();
	}
	
    // Private constructor to force the use of the Builder
    private Order(Builder builder)
    {
    	this();
        this.orderTotal = builder.orderTotal;
    }

    public void addOrderItem (OrderItem orderItem) {
    	this.orderItems.add(orderItem);
    }
    
    public static class Builder {

        private BigDecimal orderTotal = null;

        public Builder orderTotal(BigDecimal orderTotal) {
            this.orderTotal = orderTotal;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public Order build() {
            return new Order(this);
        }
    }
}