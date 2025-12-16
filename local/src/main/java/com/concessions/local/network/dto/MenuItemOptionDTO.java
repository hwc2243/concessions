package com.concessions.local.network.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for BaseMenuItemOption entity.
 */
public class MenuItemOptionDTO implements Serializable {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long organizationId;

    public MenuItemOptionDTO() {
    }
    
    public MenuItemOptionDTO(Long id, String name, String description, BigDecimal price, Long organizationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.organizationId = organizationId;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    // --- Utility Methods ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItemOptionDTO that = (MenuItemOptionDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}