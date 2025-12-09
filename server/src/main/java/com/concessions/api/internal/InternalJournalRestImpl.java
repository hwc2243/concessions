package com.concessions.api.internal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concessions.api.internal.base.BaseInternalJournalRestImpl;

@RestController
@RequestMapping("/api/internal/journal")
public class InternalJournalRestImpl
   extends BaseInternalJournalRestImpl
   implements InternalJournalRest
{
}