package com.luckycardshop.edge_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
	
	@Bean
	public KeyResolver keyResolver() {
		return exchange -> Mono.just("anonymous"); //constant key to apply rate limiting to
	}
}
