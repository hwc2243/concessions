package com.concessions.common.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class JournalSummaryDTO {

	private long orderCount = 0;
    private BigDecimal salesTotal = BigDecimal.ZERO;
    
	public JournalSummaryDTO() {
	}

	public JournalSummaryDTO (long orderCount, BigDecimal salesTotal)
	{
		this.orderCount = orderCount;
		this.salesTotal = salesTotal;
	}
	
    public void incrementCount() {
        this.orderCount++;
    }

    public void addToTotal(BigDecimal amount) {
        this.salesTotal = this.salesTotal.add(amount);
    }
    
    // Combiner method for parallel streams
    public void merge (JournalSummaryDTO other) {
        this.orderCount += other.orderCount;
        this.salesTotal = this.salesTotal.add(other.salesTotal);
    }

    // Getters
    public long getOrderCount()
    {
    	return orderCount;
    }
    
    public BigDecimal getSalesTotal()
    {
    	return salesTotal;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        JournalSummaryDTO that = (JournalSummaryDTO) o;
        
        if (orderCount != that.orderCount) return false;
        
        if (salesTotal == null) {
            return that.salesTotal == null;
        }
        
        return salesTotal.compareTo(that.salesTotal) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderCount, salesTotal);
    }
    
}
