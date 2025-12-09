package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalOrderRestImpl;

@RestController
@RequestMapping("/api/internal/order")
public class InternalOrderRestImpl
   extends BaseInternalOrderRestImpl
   implements InternalOrderRest
{
}