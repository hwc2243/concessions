package com.concessions.local.network.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.concessions.client.model.MenuItem;
import com.concessions.dto.MenuItemDTO;

public class MenuItemMapper {

	public MenuItemMapper() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Converts a MenuItem entity instance to a MenuItemDTO instance.
     * @param entity The MenuItem instance to convert.
     * @return A new MenuItemDTO instance, or null if the input entity is null.
     */
    public static MenuItemDTO toDto (MenuItem entity) {
        if (entity == null) {
            return null;
        }

        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setPrice(entity.getPrice());
        dto.setOrganizationId(entity.getOrganizationId());

        // HWC TODO menu item options are not currently supported and so they aren't loaded
        /*
        if (entity.getOptions() != null && !entity.getOptions().isEmpty()) {
            List<MenuItemOptionDTO> optionDTOs = entity.getOptions().stream()
                .map(MenuItemOptionMapper::toDto)
                .filter(optionDto -> optionDto != null)
                .collect(Collectors.toList());

            dto.setOptions(optionDTOs);
        }
        */

        return dto;
    }
}
