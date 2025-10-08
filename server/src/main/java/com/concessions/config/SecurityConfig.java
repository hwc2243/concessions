package com.concessions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.concessions.keycloak.KeycloakLogoutHandler;
import com.concessions.spring.RestContextFilter;

import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final KeycloakLogoutHandler keycloakLogoutHandler;

	private final RestContextFilter restContextFilter;
    SecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler, RestContextFilter restContextFilter) {
        this.keycloakLogoutHandler = keycloakLogoutHandler;
        this.restContextFilter = restContextFilter;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiInternalFilterChain(HttpSecurity http) throws Exception {
        http
            // This matcher ensures this chain only applies to requests starting with /api/
            .securityMatcher(new AntPathRequestMatcher("/api/internal/**"))
            .authorizeHttpRequests(requests -> requests
                .anyRequest()
                .permitAll()
            )
            // APIs should be stateless, so we disable session creation
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // CSRF protection is not needed for stateless APIs
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain apiExternalFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/api/external/**"))
            .addFilterAfter(restContextFilter, BearerTokenAuthenticationFilter.class) 
            .authorizeHttpRequests(requests -> requests
                .anyRequest()
                .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
    @Bean
    @Order(3)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    	http
        .authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/webjars/**", "/css/**", "/images/**", "/js/**")
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .oauth2Login(//Customizer.withDefaults()
        		oauth2 -> oauth2
				//.loginPage("/oauth2/authorization/keycloak")
				.defaultSuccessUrl("/home", false)
		)
        		
        .logout((logout) -> logout
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(false)
                        .addLogoutHandler(keycloakLogoutHandler)
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
              );
  
      return http.build();
    }
}
