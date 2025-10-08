package com.concessions.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.concessions.spring.ContextInterceptor;
import com.concessions.spring.RegistrationInterceptor;
import com.concessions.spring.security.ClientInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	/*
	 * private final ClientInterceptor clientInterceptor;
	 * 
	 * 
	 * @Autowired public WebConfig(ClientInterceptor clientInterceptor,
	 * RegistrationInterceptor registrationInterceptor) { this.clientInterceptor =
	 * clientInterceptor; this.registrationInterceptor = registrationInterceptor; }
	 */

	private final ContextInterceptor contextInterceptor;
	
	private final RegistrationInterceptor registrationInterceptor;

	@Autowired
	public WebConfig(ContextInterceptor contextInterceptor, RegistrationInterceptor registrationInterceptor) {	
		this.contextInterceptor = contextInterceptor;
		this.registrationInterceptor = registrationInterceptor;
	}

	@Override
	public void addInterceptors (InterceptorRegistry registry) {
		registry.addInterceptor(contextInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico");
		
		// registry.addInterceptor(registrationInterceptor).addPathPatterns("/register/**");
	}
}