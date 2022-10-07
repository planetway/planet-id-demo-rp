package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.exception.NoConsentException;
import com.planetway.fudosan.service.ConsentContainerService;
import com.planetway.fudosan.service.RaService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RestController
@RequestMapping("/api/lra")
public class LRAController {

    private final AppProperties appProperties;
    private final ConsentContainerService consentContainerService;
    private final RaService raService;
    private final OpenIdSupport openIdSupport;

    public LRAController(AppProperties appProperties, ConsentContainerService consentContainerService, RaService raService, OpenIdSupport openIdSupport) {
        this.appProperties = appProperties;
        this.consentContainerService = consentContainerService;
        this.raService = raService;
        this.openIdSupport = openIdSupport;
    }

    @GetMapping("person")
    public ResponseEntity getPersonalInfo(@AuthenticationPrincipal UserInfo userInfo) {
        try {
            return ResponseEntity.ok(raService.getPerson(userInfo.getPlanetId()));
        } catch (NoConsentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No consent");
        }
    }

    @PostMapping("consent")
    public AuthRequest createOpenIdContext(@AuthenticationPrincipal UserInfo userInfo,
                                           @RequestParam(required = false, value = "lang") String language,
                                           HttpServletResponse response) {
        String planetId = userInfo.getPlanetId();
        UUID consentUuid = UUID.randomUUID();
        String consentDocument = consentContainerService.createConsentContainerForRa(planetId, consentUuid);
        String redirectUri = appProperties.getBaseUrl() + "/api/callback/consent";
        return openIdSupport.createRequestForConsent(response, redirectUri, planetId, consentDocument);
    }
}
