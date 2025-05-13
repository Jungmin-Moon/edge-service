package com.luckycardshop.edge_service.config;

import java.security.Principal;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {
	
	@Bean
	public KeyResolver keyResolver() {
		return exchange -> exchange.getPrincipal() //we get the principal of currently logged in users
							.map(Principal::getName) //get their name
							.defaultIfEmpty("anonymous"); //if they are not authenticated then they are just anonymous
	}
}
