package com.concessions.dto;

import com.concessions.dto.StatusType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import java.util.List;

public class JournalDTO
{
  protected String id;

  protected StatusType status = null;
  
  protected Long menuId = null;
  
  protected Long orderCount = null;
  
  protected BigDecimal salesTotal = null;
  
  protected LocalDateTime startTs = null;
  
  protected LocalDateTime endTs = null;
  
  protected LocalDateTime syncTs = null;
  
  protected Long organizationId = null;
  

  public JournalDTO () {
  }
  
  // Private constructor to force the use of the Builder
  private JournalDTO (Builder builder)
  {
    this.id = builder.id;
    this.status = builder.status;
    this.menuId = builder.menuId;
    this.orderCount = builder.orderCount;
    this.salesTotal = builder.salesTotal;
    this.startTs = builder.startTs;
    this.endTs = builder.endTs;
    this.syncTs = builder.syncTs;
    this.organizationId = builder.organizationId;
  }

  public String getId ()
  {
    return this.id;
  }
  
  public void setId (String id)
  {
    this.id = id;
  }


  public StatusType getStatus ()
  {
    return this.status;
  }
  
  public void setStatus (StatusType status)
  {
    this.status = status;
  }
  
  public Long getMenuId ()
  {
    return this.menuId;
  }
  
  public void setMenuId (Long menuId)
  {
    this.menuId = menuId;
  }
  
  public Long getOrderCount ()
  {
    return this.orderCount;
  }
  
  public void setOrderCount (Long orderCount)
  {
    this.orderCount = orderCount;
  }
  
  public BigDecimal getSalesTotal ()
  {
    return this.salesTotal;
  }
  
  public void setSalesTotal (BigDecimal salesTotal)
  {
    this.salesTotal = salesTotal;
  }
  
  public LocalDateTime getStartTs ()
  {
    return this.startTs;
  }
  
  public void setStartTs (LocalDateTime startTs)
  {
    this.startTs = startTs;
  }
  
  public LocalDateTime getEndTs ()
  {
    return this.endTs;
  }
  
  public void setEndTs (LocalDateTime endTs)
  {
    this.endTs = endTs;
  }
  
  public LocalDateTime getSyncTs ()
  {
    return this.syncTs;
  }
  
  public void setSyncTs (LocalDateTime syncTs)
  {
    this.syncTs = syncTs;
  }
  
  public Long getOrganizationId ()
  {
    return this.organizationId;
  }
  
  public void setOrganizationId (Long organizationId)
  {
    this.organizationId = organizationId;
  }
  
  public static class Builder {

	private String id;
    private StatusType status = null;
    private Long menuId = null;
    private Long orderCount = null;
    private BigDecimal salesTotal = null;
    private LocalDateTime startTs = null;
    private LocalDateTime endTs = null;
    private LocalDateTime syncTs = null;
    private Long organizationId = null;

    public Builder id(String id) {
      this.id = id;
      return this;
    }
    
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
    public JournalDTO build() {
      return new JournalDTO(this);
    }
  }
}