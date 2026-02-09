package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.MenuItemOptionDTO;

import com.concessions.model.MenuItemOption;

@Mapper
public interface MenuItemOptionMapper {
  MenuItemOptionMapper INSTANCE = Mappers.getMapper(MenuItemOptionMapper.class);
  
  MenuItemOptionDTO toDto(MenuItemOption menuItemOption);
}