package com.concessions.service;

import org.springframework.stereotype.Service;

import com.concessions.service.base.BaseOrderItemServiceImpl;

import com.concessions.model.OrderItem;

@Service
public class OrderItemServiceImpl
  extends BaseOrderItemServiceImpl<OrderItem,Long>
  implements OrderItemService
{
}