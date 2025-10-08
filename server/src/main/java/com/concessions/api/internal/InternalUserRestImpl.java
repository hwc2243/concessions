package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalUserRestImpl;

@RestController
@RequestMapping("/api/internal/user")
public class InternalUserRestImpl
   extends BaseInternalUserRestImpl
   implements InternalUserRest
{
}