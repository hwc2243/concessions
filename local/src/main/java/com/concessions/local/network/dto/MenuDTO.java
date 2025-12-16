package com.concessions.local.network.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * DTO for BaseMenu entity.
 * Includes a list of MenuItemDTOs for its menuItems relationship.
 */
public class MenuDTO implements Serializable {
    
    private Long id;
    private String name;
    private String description;
    private Long organizationId;
    
    // Relationship mapped to DTO
    private List<MenuItemDTO> menuItems;

    public MenuDTO() {
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemDTO> menuItems) {
        this.menuItems = menuItems;
    }

    // --- Utility Methods ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuDTO menuDTO = (MenuDTO) o;
        return Objects.equals(id, menuDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}