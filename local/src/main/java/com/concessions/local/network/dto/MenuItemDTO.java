package com.concessions.local.network.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.concessions.client.model.CategoryType;

/**
 * DTO for BaseMenuItem entity.
 * Includes a list of MenuItemOptionDTOs for its options relationship.
 */
public class MenuItemDTO implements Serializable {
    
    private Long id;
    private String name;
    private String description;
    private CategoryType category;
    private BigDecimal price;
    private Long organizationId;
    
    // Relationship mapped to DTO
    private List<MenuItemOptionDTO> options;

    public MenuItemDTO() {
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

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
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

    public List<MenuItemOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<MenuItemOptionDTO> options) {
        this.options = options;
    }

    // --- Utility Methods ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItemDTO that = (MenuItemDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}