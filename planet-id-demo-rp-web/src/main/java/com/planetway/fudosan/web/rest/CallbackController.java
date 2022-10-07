package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.domain.SignedDocumentEntity;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.repository.SignedDocumentRepository;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.AuthResponse;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.oauth.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CallbackController {

    private final UserService userService;
    private final OpenIdSupport openIdSupport;
    private final SignedDocumentRepository signedDocumentRepository;

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

    @PostMapping("/callback/consent-revoke")
    public ResponseEntity<Object> consentRevokeCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthResponse requestBody) {
        TokenResponse tokenResponse = openIdSupport.handleCallback(request, response, requestBody);
        SignedDocumentEntity doc = signedDocumentRepository.findByConsentUuid(tokenResponse.getConsentUuid());
        doc.setRevokeDocumentUuid(tokenResponse.getPayloadUuid());
        signedDocumentRepository.save(doc);

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
