package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.UserCredentials;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.ConsentContainerService;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.AuthResponse;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.oauth.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AppController {

    private final AppProperties appProperties;
    private final AuthenticationManager authenticationManager;
    private final OpenIdSupport openIdSupport;
    private final UserService userService;
    private final ConsentContainerService consentContainerService;

    @PostMapping("/authenticate")
    public UserInfo authenticate(HttpServletRequest request, @RequestBody UserCredentials userCredentials) {
        UserInfo principal = userService.loadUserByUsername(userCredentials.getUser());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        principal, userCredentials.getPassword()
                )
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // Create a new session and add the security context.
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        return principal;
    }

    @PostMapping("/authenticate-planet-id")
    public AuthRequest authenticateWithPlanetId(HttpServletResponse response, @RequestParam(name = "lang", required = false) String language) {
        String redirectUri = appProperties.getBaseUrl() + "/api/authenticate-planet-id/callback";
        return openIdSupport.createRequestForAuthenticate(response, redirectUri);
    }

    @PostMapping("/authenticate-planet-id/callback")
    public UserInfo authenticateWithPlanetIdCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthResponse requestBody) {
        log.info("{}", requestBody);

        TokenResponse tokenResponse = openIdSupport.handleCallback(request, response, requestBody);

        userService.loginUserWithPlanetId(tokenResponse.getPlanetId());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return (UserInfo) securityContext.getAuthentication().getPrincipal();
    }

    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "\"OK\"";
    }

    @GetMapping("/revoke-consent-request")
    public AuthRequest getRevokeConsentRequest(@AuthenticationPrincipal UserInfo userInfo, @RequestParam(name = "consentUuid") String consentUuid, HttpServletResponse response) {
        String planetId = userInfo.getPlanetId();
        String consentRevokeDocument = consentContainerService.createConsentRevokeDocument(consentUuid, planetId);
        String redirectUri = appProperties.getBaseUrl() + "/api/callback/consent-revoke";
        return openIdSupport.createRequestForConsentRevoke(response, redirectUri, planetId, consentRevokeDocument);
    }
}
