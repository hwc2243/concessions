package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalAddressRestImpl;

@RestController
@RequestMapping("/api/external/address")
public class ExternalAddressRestImpl
   extends BaseExternalAddressRestImpl
   implements ExternalAddressRest
{
}