package com.concessions.client.model;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.concessions.client.model.StatusType;
import java.math.BigDecimal;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


import com.concessions.client.model.base.BaseJournal;

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
        this.startTs = builder.startTs;
        this.endTs = builder.endTs;
        this.status = builder.status;
        this.salesTotal = builder.salesTotal;
        this.organizationId = builder.organizationId;
    }

    public static class Builder {

        private LocalDateTime startTs = null;
        private LocalDateTime endTs = null;
        private StatusType status = null;
        private BigDecimal salesTotal = null;
        private Long organizationId = null;

        public Builder startTs(LocalDateTime startTs) {
            this.startTs = startTs;
            return this;
        }

        public Builder endTs(LocalDateTime endTs) {
            this.endTs = endTs;
            return this;
        }

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder salesTotal(BigDecimal salesTotal) {
            this.salesTotal = salesTotal;
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