package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalAddressRestImpl;

@RestController
@RequestMapping("/api/internal/address")
public class InternalAddressRestImpl
   extends BaseInternalAddressRestImpl
   implements InternalAddressRest
{
}