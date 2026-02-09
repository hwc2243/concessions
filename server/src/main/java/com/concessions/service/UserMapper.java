package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.UserDTO;

import com.concessions.model.User;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  
  UserDTO toDto(User user);
}