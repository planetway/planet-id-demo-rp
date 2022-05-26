package com.planetway.fudosan.web;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockRpUser {
    long id() default 0L;
    String username() default "test@test";
    String planetId() default "123456789000";
}
