package com.concessions.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseOrder;

@Entity
@Table(name="order_")
public class Order extends BaseOrder<Order>
    implements Serializable
{
	public Order ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private Order(Builder builder)
    {
        this.journalId = builder.journalId;
        this.menuId = builder.menuId;
        this.orderTotal = builder.orderTotal;
        this.startTs = builder.startTs;
        this.endTs = builder.endTs;
    }

    public static class Builder {

        private String journalId = null;
        private Long menuId = null;
        private BigDecimal orderTotal = null;
        private LocalDateTime startTs = null;
        private LocalDateTime endTs = null;

        public Builder journalId(String journalId) {
            this.journalId = journalId;
            return this;
        }

        public Builder menuId(Long menuId) {
            this.menuId = menuId;
            return this;
        }

        public Builder orderTotal(BigDecimal orderTotal) {
            this.orderTotal = orderTotal;
            return this;
        }

        public Builder startTs(LocalDateTime startTs) {
            this.startTs = startTs;
            return this;
        }

        public Builder endTs(LocalDateTime endTs) {
            this.endTs = endTs;
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