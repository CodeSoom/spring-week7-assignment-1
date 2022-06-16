package com.codesoom.assignment.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.UserAuthentication;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final AuthenticationService authenticationService;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
		AuthenticationService authenticationService) {
		super(authenticationManager);
		this.authenticationService = authenticationService;
	}

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

		String authorization = request.getHeader("Authorization");

		if (authorization != null) {
			try {
				String accessToken = authorization.substring("Bearer ".length());
				Long userId = authenticationService.parseToken(accessToken);
				UserAuthentication authentication = new UserAuthentication(userId);

				final SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(authentication);
			} catch (Exception e) {
				throw new InvalidTokenException(authorization);
			}
		}
		chain.doFilter(request, response);
	}
}
