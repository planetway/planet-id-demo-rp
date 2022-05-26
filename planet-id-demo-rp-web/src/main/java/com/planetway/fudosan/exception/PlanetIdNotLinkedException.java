package com.planetway.fudosan.exception;


import org.springframework.security.core.AuthenticationException;

public class PlanetIdNotLinkedException extends AuthenticationException {
    public PlanetIdNotLinkedException(String explanation) {
        super(explanation);
    }
}
