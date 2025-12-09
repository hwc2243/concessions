package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseLocationServiceImpl;

import com.concessions.client.model.Location;

@Service
public class LocationServiceImpl
  extends BaseLocationServiceImpl<Location,Long>
  implements LocationService
{
}