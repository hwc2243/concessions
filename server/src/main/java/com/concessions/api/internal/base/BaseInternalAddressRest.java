package com.concessions.api.internal.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestParam;

import com.concessions.model.Address;

import com.concessions.api.internal.base.BaseInternalAddressRest;

public interface BaseInternalAddressRest
{
	public ResponseEntity<Address> createAddress (Address address);
	
    public ResponseEntity<Address> deleteAddress(Long id); 

	public ResponseEntity<Address> getAddress (Long id);
	
	public ResponseEntity<List<Address>> listAddresss ();
	
	public ResponseEntity<List<Address>> searchAddresss (
	);
	
    public ResponseEntity<Address> updateAddress (Long id, Address address);
}