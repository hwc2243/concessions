package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalMenuRestImpl;

@RestController
@RequestMapping("/api/external/menu")
public class ExternalMenuRestImpl
   extends BaseExternalMenuRestImpl
   implements ExternalMenuRest
{
}