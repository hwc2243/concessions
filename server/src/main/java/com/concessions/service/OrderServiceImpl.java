package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseOrderServiceImpl;

import com.concessions.model.Order;

@Service
public class OrderServiceImpl
  extends BaseOrderServiceImpl<Order,String>
  implements OrderService
{
}