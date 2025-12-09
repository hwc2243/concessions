package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalOrderRestImpl;

@RestController
@RequestMapping("/api/external/order")
public class ExternalOrderRestImpl
   extends BaseExternalOrderRestImpl
   implements ExternalOrderRest
{
}