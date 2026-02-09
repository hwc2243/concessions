package com.concessions.local.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.local.dto.DeviceDTO;

import com.concessions.local.model.Device;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
  DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);
  
  DeviceDTO toDto(Device device);
}