package com.concessions.local.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DynamicBeanService {
	
	@Autowired
    private GenericApplicationContext context;
	
	/**
     * Dynamically registers a bean in the Spring context at runtime.
     * @param <T>       The type of the bean
     * @param beanName  The unique name for the bean
     * @param beanClass The class type to instantiate
     * @return          The newly created bean instance
     */
    public <T> T createBean(String beanName, Class<T> beanClass) {
        // Register the bean definition
        context.registerBean(beanName, beanClass);
        
        // Retrieve and return the instance (Spring will have performed injection)
        return context.getBean(beanName, beanClass);
    }
}
