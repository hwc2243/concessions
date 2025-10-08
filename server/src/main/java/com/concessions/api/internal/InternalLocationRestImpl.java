package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalLocationRestImpl;

@RestController
@RequestMapping("/api/internal/location")
public class InternalLocationRestImpl
   extends BaseInternalLocationRestImpl
   implements InternalLocationRest
{
}