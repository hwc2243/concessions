package com.concessions.client.service;

import org.springframework.stereotype.Service;

import com.concessions.client.service.base.BaseAddressServiceImpl;

import com.concessions.client.model.Address;

@Service
public class AddressServiceImpl
  extends BaseAddressServiceImpl<Address,Long>
  implements AddressService
{
}