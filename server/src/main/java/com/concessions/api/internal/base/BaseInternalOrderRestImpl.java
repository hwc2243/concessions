package com.concessions.api.internal.base;

import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Order;

import com.concessions.service.OrderService;
import com.concessions.service.ServiceException;

import com.concessions.api.internal.base.BaseInternalOrderRest;

public abstract class BaseInternalOrderRestImpl implements BaseInternalOrderRest
{
	@Autowired
	protected OrderService orderService;

	@PostMapping
	@Override
	public ResponseEntity<Order> createOrder (@RequestBody Order order)
	{
	  Order newOrder = null;
	  
	  try
	  {
	    newOrder = orderService.create(order);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Order> deleteOrder(@PathVariable String id) 
    {
      try
      {
        orderService.delete(id);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.noContent().build();
    }

	@GetMapping("/{id}")
	@Override
	public ResponseEntity<Order> getOrder (@PathVariable String id)
	{
		Order order = null;
		
		try	{
			order = orderService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Order>> listOrders ()
    {
        try
        {
        	List<Order> orders = orderService.findAll();
        	return ResponseEntity.ok(orders);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Order>> searchOrders (
	)
	{
	  try
	  {
	    List<Order> allOrders = orderService.findAll();
	    List<Order> filteredOrders = allOrders.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredOrders);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @RequestBody Order order)
    {
      Order updatedOrder = null;
      
      if (order.getId() == null || "".equals(order.getId()))
      {
      	order.setId(id);
      }
      else if (!order.getId().equals(id))
      {
        return ResponseEntity.badRequest().build();
      }
      try
      {
        Order existingOrder = orderService.get(id);
        if (existingOrder == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedOrder = orderService.update(order);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedOrder);
    }
}