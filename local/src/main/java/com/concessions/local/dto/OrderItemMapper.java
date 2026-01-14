package com.concessions.local.dto;

import java.util.UUID;

import com.concessions.client.model.OrderItem;
import com.concessions.dto.OrderItemDTO;

public class OrderItemMapper {

	/**
     * Converts a BaseOrderItem entity to a BaseOrderItemDTO.
     * @param entity The BaseOrderItem to convert.
     * @return The resulting BaseOrderItemDTO.
     */
    public static OrderItemDTO toDto(OrderItem entity) {
        if (entity == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(entity.getId());
        dto.setMenuItemId(entity.getMenuItemId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());

        return dto;
    }

    /**
     * Converts a BaseOrderItemDTO to a BaseOrderItem entity.
     * NOTE: This method returns the abstract BaseOrderItem. In a real application,
     * you would typically instantiate a concrete subclass of BaseOrderItem.
     * @param dto The BaseOrderItemDTO to convert.
     * @return The resulting BaseOrderItem entity.
     */
    public static OrderItem fromDto (OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        
        OrderItem entity = new OrderItem();
        entity.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID().toString());
        entity.setMenuItemId(dto.getMenuItemId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());

        return entity;
    }
}
