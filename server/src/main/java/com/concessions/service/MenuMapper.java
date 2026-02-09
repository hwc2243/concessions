package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.MenuDTO;

import com.concessions.model.Menu;

@Mapper
public interface MenuMapper {
  MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);
  
  MenuDTO toDto(Menu menu);
}