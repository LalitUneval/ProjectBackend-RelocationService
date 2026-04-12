package com.lalit.relocationservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

// NO @Component - registered manually in FilterConfig
public class TokenCaptureFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null) {
            UserTokenHolder.setToken(authHeader);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            UserTokenHolder.clear();
        }
    }
}