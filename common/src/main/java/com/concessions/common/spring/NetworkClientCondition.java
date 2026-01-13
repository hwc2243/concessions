package com.concessions.common.spring;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NetworkClientCondition implements Condition {

	public NetworkClientCondition() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("local.network.client");
        return "true".equalsIgnoreCase(property);
    }
}
