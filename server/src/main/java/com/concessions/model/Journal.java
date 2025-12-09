package com.concessions.model;

import com.concessions.model.StatusType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.model.base.BaseJournal;

@Entity
@Table(name="journal")
public class Journal extends BaseJournal<Journal>
    implements Serializable
{
	public Journal ()
	{
		super();
	}
	
    // Private constructor to force the use of the Builder
    private Journal(Builder builder)
    {
        this.status = builder.status;
        this.menuId = builder.menuId;
        this.orderCount = builder.orderCount;
        this.salesTotal = builder.salesTotal;
        this.startTs = builder.startTs;
        this.endTs = builder.endTs;
        this.syncTs = builder.syncTs;
        this.organizationId = builder.organizationId;
    }

    public static class Builder {

        private StatusType status = null;
        private Long menuId = null;
        private Long orderCount = null;
        private BigDecimal salesTotal = null;
        private LocalDateTime startTs = null;
        private LocalDateTime endTs = null;
        private LocalDateTime syncTs = null;
        private Long organizationId = null;

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder menuId(Long menuId) {
            this.menuId = menuId;
            return this;
        }

        public Builder orderCount(Long orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public Builder salesTotal(BigDecimal salesTotal) {
            this.salesTotal = salesTotal;
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

        public Builder syncTs(LocalDateTime syncTs) {
            this.syncTs = syncTs;
            return this;
        }

        public Builder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        /**
         * The build method creates and returns the immutable Entity object.
         */
        public Journal build() {
            return new Journal(this);
        }
    }
}