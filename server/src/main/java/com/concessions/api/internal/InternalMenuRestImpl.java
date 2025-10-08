package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalMenuRestImpl;

@RestController
@RequestMapping("/api/internal/menu")
public class InternalMenuRestImpl
   extends BaseInternalMenuRestImpl
   implements InternalMenuRest
{
}