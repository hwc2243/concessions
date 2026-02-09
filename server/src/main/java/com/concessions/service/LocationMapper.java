package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.LocationDTO;

import com.concessions.model.Location;

@Mapper
public interface LocationMapper {
  LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);
  
  LocationDTO toDto(Location location);
}