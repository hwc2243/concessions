package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseAddressServiceImpl;

import com.concessions.model.Address;

@Service
public class AddressServiceImpl
  extends BaseAddressServiceImpl<Address,Long>
  implements AddressService
{
}