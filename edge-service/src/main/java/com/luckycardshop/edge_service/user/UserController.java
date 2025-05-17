package com.luckycardshop.edge_service.user;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class UserController {
	
	@GetMapping("user") //thanks to the AuthenticationPrincipal annotation we do not have to bloat the method more and can just hard create user object
	public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) {
		var user = new User(
				oidcUser.getPreferredUsername(),
				oidcUser.getGivenName(),
				oidcUser.getFamilyName(),
				oidcUser.getClaimAsStringList("roles") //now instead of being hard coded, we jsut get it from the ID Token
			);
		
		return Mono.just(user);
	}
}
