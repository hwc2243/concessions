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

import com.concessions.model.Address;

import com.concessions.service.AddressService;
import com.concessions.service.ServiceException;

import com.concessions.api.internal.base.BaseInternalAddressRest;

public abstract class BaseInternalAddressRestImpl implements BaseInternalAddressRest
{
	@Autowired
	protected AddressService addressService;

	@PostMapping
	@Override
	public ResponseEntity<Address> createAddress (@RequestBody Address address)
	{
	  Address newAddress = null;
	  
	  try
	  {
	    newAddress = addressService.create(address);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	  
	  return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
	}
	
	@DeleteMapping("/{id}")
	@Override
    public ResponseEntity<Address> deleteAddress(@PathVariable Long id) 
    {
      try
      {
        addressService.delete(id);
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
	public ResponseEntity<Address> getAddress (@PathVariable Long id)
	{
		Address address = null;
		
		try	{
			address = addressService.get(id);
		}
		catch (ServiceException ex) {
			ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
		}
		
        if (address != null) {
            return ResponseEntity.ok(address);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Override
    public ResponseEntity<List<Address>> listAddresss ()
    {
        try
        {
        	List<Address> addresss = addressService.findAll();
        	return ResponseEntity.ok(addresss);
        }
        catch (ServiceException ex)
        {
          ex.printStackTrace();
          return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    @Override  
    public ResponseEntity<List<Address>> searchAddresss (
	)
	{
	  try
	  {
	    List<Address> allAddresss = addressService.findAll();
	    List<Address> filteredAddresss = allAddresss.stream()
          .collect(Collectors.toList());
          
        return ResponseEntity.ok(filteredAddresss);
	  }
	  catch (ServiceException ex)
	  {
	    ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
	  }
	}
	
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address)
    {
      Address updatedAddress = null;
      
      if (address.getId() == 0) 
      {
      	address.setId(id);
      }
      else if (id != address.getId())
      {
         return ResponseEntity.badRequest().build();
      }
      try
      {
        Address existingAddress = addressService.get(id);
        if (existingAddress == null)
        {
          return ResponseEntity.notFound().build();
        }
        updatedAddress = addressService.update(address);
      }
      catch (ServiceException ex)
      {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
      }
      return ResponseEntity.ok(updatedAddress);
    }
}