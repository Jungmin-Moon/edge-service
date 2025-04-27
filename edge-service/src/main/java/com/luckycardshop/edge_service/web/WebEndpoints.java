package com.luckycardshop.edge_service.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class WebEndpoints {
	
	/*
	 * Functional REST endpoint which are all defined in beans
	 */
	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions.route()
				//fall back for GET requests
				.GET("/catalog-fallback", request -> ServerResponse.ok().body(Mono.just(""), String.class)) //compared to the book we don't need the String.class parameter 
				
				//fallback for POST requests
				.POST("/catalog-fallback", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
				.build(); //builds the functional endpoints in the end
	}
}
