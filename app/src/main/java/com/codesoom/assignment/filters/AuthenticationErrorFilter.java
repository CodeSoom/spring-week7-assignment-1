package com.codesoom.assignment.filters;

import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import java.io.IOException;

public class AuthenticationErrorFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }
}
