package com.codesoom.assignment.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Generated;

public class UserAuthentication extends AbstractAuthenticationToken {

	private final Long userId;

	public UserAuthentication(Long userId) {
		super(authorities());
		this.userId = userId;
	}

	@Generated
	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userId;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	private static List<? extends GrantedAuthority> authorities() {
		return Arrays.asList(new SimpleGrantedAuthority("USER"));
	}
}
