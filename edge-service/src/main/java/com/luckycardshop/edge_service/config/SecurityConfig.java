package com.luckycardshop.edge_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;

import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class SecurityConfig {

	
	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
		return http.authorizeExchange(exchange -> exchange.pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
										.pathMatchers(HttpMethod.GET, "/cards/**").permitAll().anyExchange().authenticated())
				.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
				.oauth2Login(Customizer.withDefaults()) //we are using oauth2 login instead
				.logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))) //custom logout handler
				.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())) //used for Angular frontends
				.build();
	}
	
	@Bean //purpose is to sub to the CsrfToken reactive stream and ensures the value is extracted correctly
	WebFilter csrfWebFilter() {
		return (exchange, chain) -> {
			exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
				Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
				return csrfToken != null ? csrfToken.then() : Mono.empty();
			}));
			
			return chain.filter(exchange);
		};
	}
	
	@Bean //used to store Access Tokens in the web session
	ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
		return new WebSessionServerOAuth2AuthorizedClientRepository();
	}
	
	
	/*
	 * This method telling SPring Security where to redirect users after logging out
	 */
	private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
		var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
		
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
		
		return oidcLogoutSuccessHandler;
	}
	
	
}
