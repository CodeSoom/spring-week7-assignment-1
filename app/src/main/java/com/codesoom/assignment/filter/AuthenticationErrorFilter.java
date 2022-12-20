package com.codesoom.assignment.filter;

import com.codesoom.assignment.errors.InvalidTokenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthenticationErrorFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch(InvalidTokenException e) {
            response.sendError(SC_UNAUTHORIZED);
        }
    }
}
