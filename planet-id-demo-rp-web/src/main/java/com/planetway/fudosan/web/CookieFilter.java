package com.planetway.fudosan.web;

import com.planetway.fudosan.configuration.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@Component
public class CookieFilter extends OncePerRequestFilter {

    private final boolean useSecureCookies;

    public CookieFilter(AppProperties appProperties) {
        useSecureCookies = appProperties.getBaseUrl().startsWith("https://");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(response) {
            @Override
            public void addCookie(Cookie cookie) {
                if (cookie.getPath() == null) {
                    cookie.setPath("/");
                }
                cookie.setHttpOnly(true);
                if (useSecureCookies) {
                    cookie.setSecure(true);
                }
                super.addCookie(cookie);
            }
        };

        filterChain.doFilter(request, wrappedResponse);
    }
}
