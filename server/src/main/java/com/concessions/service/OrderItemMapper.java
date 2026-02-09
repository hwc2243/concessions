package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.OrderItemDTO;

import com.concessions.model.OrderItem;

@Mapper
public interface OrderItemMapper {
  OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);
  
  OrderItemDTO toDto(OrderItem orderItem);
}