package com.concessions.local.dto;

import com.concessions.client.model.MenuItemOption;
import com.concessions.dto.MenuItemOptionDTO;

public class MenuItemOptionMapper {

	public MenuItemOptionMapper() {
		// TODO Auto-generated constructor stub
	}

	   /**
     * Converts a MenuItemOption entity to a MenuItemOptionDTO.
     * @param entity The MenuItemOption entity.
     * @return The MenuItemOptionDTO.
     */
    public static MenuItemOptionDTO toDto (MenuItemOption entity) {
        if (entity == null) {
            return null;
        }

        MenuItemOptionDTO dto = new MenuItemOptionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setOrganizationId(entity.getOrganizationId());

        return dto;
    }
}
