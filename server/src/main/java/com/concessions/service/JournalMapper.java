package com.concessions.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.concessions.dto.JournalDTO;

import com.concessions.model.Journal;

@Mapper
public interface JournalMapper {
  JournalMapper INSTANCE = Mappers.getMapper(JournalMapper.class);
  
  JournalDTO toDto(Journal journal);
}