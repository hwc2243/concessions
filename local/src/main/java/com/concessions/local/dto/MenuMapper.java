package com.concessions.local.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.concessions.client.model.Menu;
import com.concessions.dto.MenuDTO;
import com.concessions.dto.MenuItemDTO;

public class MenuMapper {

	public MenuMapper() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Converts a BaseMenu entity instance to a MenuDTO instance.
     * * @param entity The BaseMenu instance to convert.
     * @return A new MenuDTO instance, or null if the input entity is null.
     */
    public static MenuDTO toDto(Menu entity) {
        if (entity == null) {
            return null;
        }

        MenuDTO dto = new MenuDTO();

        // 1. Map simple fields
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setOrganizationId(entity.getOrganizationId());

        // 2. Map collection/relationship fields recursively
        if (entity.getMenuItems() != null && !entity.getMenuItems().isEmpty()) {
            List<MenuItemDTO> menuItemDTOs = entity.getMenuItems().stream()
                // Use the dedicated MenuItemMapper to convert each item
                .map(MenuItemMapper::toDto)
                .filter(itemDto -> itemDto != null) // Filter out any null results
                .collect(Collectors.toList());
            
            dto.setMenuItems(menuItemDTOs);
        }

        return dto;
    }
}
