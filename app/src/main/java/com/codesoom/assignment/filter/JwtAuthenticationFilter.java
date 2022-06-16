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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

		if (filterWithPathAndMethod(request)) {
			chain.doFilter(request, response);
			return;
		}
		if (authorization == null) {
			throw new InvalidTokenException(null);
		}
		String accessToken = authorization.substring("Bearer ".length());

		if (authorization != null) {
			try {
				Long userId = authenticationService.parseToken(accessToken);
				UserAuthentication authentication = new UserAuthentication(userId);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(authentication);

			} catch (Exception e) {
				throw new InvalidTokenException(authorization);
			}
		}
		chain.doFilter(request, response);
	}
	private boolean filterWithPathAndMethod(HttpServletRequest request) {
		String path = request.getRequestURI();
		if (!path.startsWith("/products")) {
			return true;
		}

		String method = request.getMethod();
		if (method.equals("GET")) {
			return true;
		}

		if (method.equals("OPTIONS")) {
			return true;
		}

		return false;
	}
}
