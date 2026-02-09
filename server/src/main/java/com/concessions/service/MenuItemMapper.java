package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.MenuItemDTO;

import com.concessions.model.MenuItem;

@Mapper
public interface MenuItemMapper {
  MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);
  
  MenuItemDTO toDto(MenuItem menuItem);
}