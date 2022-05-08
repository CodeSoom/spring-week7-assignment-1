package com.codesoom.assignment.filters;

import com.codesoom.assignment.errors.InvalidTokenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationErrorFilter extends HttpFilter {
    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        try {
            super.doFilter(request, response, chain);
        } catch (InvalidTokenException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
