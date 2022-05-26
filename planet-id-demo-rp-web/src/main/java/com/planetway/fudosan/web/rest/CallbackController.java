package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.AuthResponse;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.oauth.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api")
public class CallbackController {

    private final UserService userService;
    private final OpenIdSupport openIdSupport;

    public CallbackController(UserService userService, OpenIdSupport openIdSupport) {
        this.userService = userService;
        this.openIdSupport = openIdSupport;
    }

    @PostMapping("/callback/link")
    public UserInfo linkWithPlanetId(
            @AuthenticationPrincipal UserInfo userInfo,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthResponse requestBody) {

        TokenResponse tokenResponse = openIdSupport.handleCallback(request, response, requestBody);

        return userService.linkPlanetId(userInfo, tokenResponse.getPlanetId());
    }

    @PostMapping("/callback/consent")
    public ResponseEntity<Object> consentCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthResponse requestBody) {
        openIdSupport.handleCallback(request, response, requestBody);
        // Save signed container, etc
        // demo application ignores this part

        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/callback/sign")
    public ResponseEntity<Object> signCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthResponse requestBody) {
        openIdSupport.handleCallback(request, response, requestBody);
        // Save signed container, etc
        // demo application ignores this part

        return ResponseEntity.noContent()
                .build();
    }
}
