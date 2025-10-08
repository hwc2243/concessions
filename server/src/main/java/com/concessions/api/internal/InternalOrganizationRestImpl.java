package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalOrganizationRestImpl;

@RestController
@RequestMapping("/api/internal/organization")
public class InternalOrganizationRestImpl
   extends BaseInternalOrganizationRestImpl
   implements InternalOrganizationRest
{
}