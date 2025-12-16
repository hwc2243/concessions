package com.concessions.local.network.dto;

import com.concessions.client.model.Journal;

public class JournalMapper {

	public JournalMapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Converts a BaseJournal entity (or any of its concrete subclasses) to a
	 * BaseJournalDTO.
	 * 
	 * @param entity The BaseJournal entity.
	 * @return The BaseJournalDTO.
	 */
	public static JournalDTO toDto(Journal entity) {
		if (entity == null) {
			return null;
		}

		JournalDTO dto = new JournalDTO();

		// Map all fields from the abstract base class
		dto.setId(entity.getId());
		dto.setStatus(entity.getStatus());
		dto.setMenuId(entity.getMenuId());
		dto.setOrderCount(entity.getOrderCount());
		dto.setSalesTotal(entity.getSalesTotal());
		dto.setStartTs(entity.getStartTs());
		dto.setEndTs(entity.getEndTs());
		dto.setSyncTs(entity.getSyncTs());
		dto.setOrganizationId(entity.getOrganizationId());

		return dto;
	}
	
	/**
     * Populates an existing BaseJournal entity with data from the BaseJournalDTO.
     * Note: Since BaseJournal is abstract, this method requires an already instantiated
     * concrete subclass to populate.
     * * @param dto The BaseJournalDTO containing the data.
     * @param entity The existing BaseJournal entity (or subclass) to populate.
     * @param <T> The concrete type extending BaseJournal.
     * @return The populated entity instance.
     */
    public static Journal fromDto (JournalDTO dto) {
        if (dto == null) {
            return null;
        }

        Journal entity = new Journal();
        // Map all fields back to the abstract base class
        entity.setId(dto.getId()); // Note: Setting ID on an existing entity might be restricted in some persistence frameworks
        entity.setStatus(dto.getStatus());
        entity.setMenuId(dto.getMenuId());
        entity.setOrderCount(dto.getOrderCount());
        entity.setSalesTotal(dto.getSalesTotal());
        entity.setStartTs(dto.getStartTs());
        entity.setEndTs(dto.getEndTs());
        entity.setSyncTs(dto.getSyncTs());
        entity.setOrganizationId(dto.getOrganizationId());

        return entity;
    }
}
