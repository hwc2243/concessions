package com.concessions.local.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.concessions.client.model.Order;
import com.concessions.client.model.OrderItem;
import com.concessions.dto.OrderDTO;

public class OrderMapper {

	/**
     * Converts a BaseOrder entity to a BaseOrderDTO, mapping nested order items.
     * @param entity The BaseOrder to convert.
     * @return The resulting BaseOrderDTO.
     */
    public static OrderDTO toDto (Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setJournalId(entity.getJournalId());
        dto.setMenuId(entity.getMenuId());
        dto.setOrderTotal(entity.getOrderTotal());
        dto.setStartTs(entity.getStartTs());
        dto.setEndTs(entity.getEndTs());

        // Map the List<OrderItem> to List<OrderItemDTO>
        if (entity.getOrderItems() != null) {
            dto.setOrderItems(entity.getOrderItems().stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Converts a BaseOrderDTO to a BaseOrder entity, mapping nested order items.
     * NOTE: This method returns the abstract BaseOrder. In a real application,
     * you would typically instantiate a concrete subclass of BaseOrder.
     * @param dto The BaseOrderDTO to convert.
     * @return The resulting BaseOrder entity.
     */
    public static Order fromDto (OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        // In a real application, replace 'new BaseOrder()' with 'new ConcreteOrder()'
        Order entity = new Order();

        entity.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID().toString());
        entity.setJournalId(dto.getJournalId());
        entity.setMenuId(dto.getMenuId());
        entity.setOrderTotal(dto.getOrderTotal());
        entity.setStartTs(dto.getStartTs());
        entity.setEndTs(dto.getEndTs());

        // Map the List<BaseOrderItemDTO> back to List<OrderItem>
        if (dto.getOrderItems() != null) {
            List<OrderItem> orderItems = dto.getOrderItems().stream()
                .map(itemDto -> {
                    // We map the DTO back to a BaseOrderItem, then cast to OrderItem 
                    // (assuming OrderItem is the concrete class used in the entity field)
                    return (OrderItem) OrderItemMapper.fromDto(itemDto);
                })
                .collect(Collectors.toList());
            
            entity.setOrderItems(orderItems);
        }

        return entity;
    }
}
