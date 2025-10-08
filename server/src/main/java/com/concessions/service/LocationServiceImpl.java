package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseLocationServiceImpl;

import com.concessions.model.Location;

@Service
public class LocationServiceImpl
  extends BaseLocationServiceImpl<Location,Long>
  implements LocationService
{
}