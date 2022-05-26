package com.planetway.fudosan.web;

import com.planetway.fudosan.domain.UserInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockRpUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockRpUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserInfo userInfo = new UserInfo(user.id(), user.username(), "", Collections.emptyList(), user.planetId());
        Authentication auth = new UsernamePasswordAuthenticationToken(userInfo, "", userInfo.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
