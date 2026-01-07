package com.concessions.local.network.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.concessions.dto.StatusType;


public class JournalDTO {

    private String id;
    private StatusType status;
    private Long menuId;
    private Long orderCount;
    private BigDecimal salesTotal;
    private LocalDateTime startTs;
    private LocalDateTime endTs;
    private LocalDateTime syncTs;
    private Long organizationId;

    // --- Constructors ---
	public JournalDTO() {
		// TODO Auto-generated constructor stub
	}

	// --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(BigDecimal salesTotal) {
        this.salesTotal = salesTotal;
    }

    public LocalDateTime getStartTs() {
        return startTs;
    }

    public void setStartTs(LocalDateTime startTs) {
        this.startTs = startTs;
    }

    public LocalDateTime getEndTs() {
        return endTs;
    }

    public void setEndTs(LocalDateTime endTs) {
        this.endTs = endTs;
    }

    public LocalDateTime getSyncTs() {
        return syncTs;
    }

    public void setSyncTs(LocalDateTime syncTs) {
        this.syncTs = syncTs;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    /**
     * Generates a hash code based only on the 'id' field.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Checks equality based only on the 'id' field.
     * Two JournalDTO objects are equal if their IDs are equal.
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JournalDTO other = (JournalDTO) obj;
        return Objects.equals(id, other.id);
    }
}
