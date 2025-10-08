package com.concessions.api.external;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.external.base.BaseExternalOrganizationRestImpl;

@RestController
@RequestMapping("/api/external/organization")
public class ExternalOrganizationRestImpl
   extends BaseExternalOrganizationRestImpl
   implements ExternalOrganizationRest
{
}