package com.concessions.api.external.base;

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

import com.concessions.api.external.base.BaseExternalAddressRest;

public abstract class BaseExternalAddressRestImpl implements BaseExternalAddressRest
{
	@Autowired
	protected AddressService addressService;

	

	
}