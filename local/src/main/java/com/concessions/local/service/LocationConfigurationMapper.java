package com.concessions.local.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.concessions.local.dto.LocationConfigurationDTO;

import com.concessions.local.model.LocationConfiguration;

@Mapper
public interface LocationConfigurationMapper {
  LocationConfigurationMapper INSTANCE = Mappers.getMapper(LocationConfigurationMapper.class);
  
  LocationConfigurationDTO toDto(LocationConfiguration locationConfiguration);
}