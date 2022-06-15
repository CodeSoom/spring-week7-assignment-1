package com.codesoom.assignment.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.UserAuthentication;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final AuthenticationService authenticationService;

	public JwtAuthenticationFilter(
		AuthenticationManager authenticationManager,
		AuthenticationService authenticationService) {
		super(authenticationManager);
		this.authenticationService = authenticationService;
	}

	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String accessToken = authorization.substring("Bearer ".length());

		if (authorization != null) {
			try {
				Long userId = authenticationService.parseToken(accessToken);
				Authentication authentication = new UserAuthentication(userId);
				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(authentication);
			} catch (Exception e) {
				throw new InvalidTokenException("test");
			}
		}
		chain.doFilter(request, response);
	}
}
