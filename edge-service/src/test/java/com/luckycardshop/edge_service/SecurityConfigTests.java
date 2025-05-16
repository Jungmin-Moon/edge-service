package com.luckycardshop.edge_service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.luckycardshop.edge_service.config.SecurityConfig;

import reactor.core.publisher.Mono;

@WebFluxTest
@Import(SecurityConfig.class)
public class SecurityConfigTests {
	
	@Autowired
	WebTestClient webClient;
	
	@MockitoBean
	ReactiveClientRegistrationRepository clientRegistrationRepository;
	
	
	@Test //simple tests that when a user logs out a 302 response is returned with the correct OIDC login and CSRF context
	void whenLogoutAuthenticatedAndWithCsrfTokenThen302() {
		when(clientRegistrationRepository.findByRegistrationId("test"))
				.thenReturn(Mono.just(testClientRegistration()));
		
		webClient.mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
				.mutateWith(SecurityMockServerConfigurers.csrf())
				.post()
				.uri("/logout")
				.exchange()
				.expectStatus().isFound();
	}
	
	//used to contact Keycloak to make sure the above method returns the right OIDC login and CSRF context
	private ClientRegistration testClientRegistration() {
		return ClientRegistration.withRegistrationId("test")
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.clientId("test")
				.authorizationUri("https://sso.luckycardshop.com/auth")
				.tokenUri("https://sso.luckycardshop.com/token")
				.redirectUri("https://luckycardshop.com")
				.build();
	}
}
