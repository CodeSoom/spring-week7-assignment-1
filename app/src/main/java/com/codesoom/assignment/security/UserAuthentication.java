package com.codesoom.assignment.security;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {
	private final Long userId;

	public UserAuthentication(Long userId){
		super(authorities());
		this.userId = userId;
	}

	private static List<GrantedAuthority> authorities() {
				return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userId;
	}
}
