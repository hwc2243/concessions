package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalLocationRestImpl;

@RestController
@RequestMapping("/api/external/location")
public class ExternalLocationRestImpl
   extends BaseExternalLocationRestImpl
   implements ExternalLocationRest
{
}