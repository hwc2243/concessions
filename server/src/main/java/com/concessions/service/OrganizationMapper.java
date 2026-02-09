package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.OrganizationDTO;

import com.concessions.model.Organization;

@Mapper
public interface OrganizationMapper {
  OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);
  
  OrganizationDTO toDto(Organization organization);
}