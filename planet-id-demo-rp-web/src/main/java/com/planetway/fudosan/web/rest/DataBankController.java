package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.ConsentContainerService;
import com.planetway.fudosan.service.DataBankService;
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
import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RestController
@RequestMapping("/api/data-bank")
public class DataBankController {

    private final AppProperties appProperties;
    private final ConsentContainerService consentContainerService;
    private final DataBankService dataBankService;
    private final OpenIdSupport openIdSupport;

    public DataBankController(AppProperties appProperties, ConsentContainerService consentContainerService, DataBankService dataBankService, OpenIdSupport openIdSupport) {
        this.appProperties = appProperties;
        this.consentContainerService = consentContainerService;
        this.dataBankService = dataBankService;
        this.openIdSupport = openIdSupport;
    }

    @GetMapping("/{dataBank}/person")
    public ResponseEntity getPersonalInfo(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable String dataBank,
            HttpServletResponse response) {
        String planetId = userInfo.getPlanetId();

        // for using after redirect from pcore
        response.addCookie(new Cookie("dataBankName", dataBank));
        Map<String, String> responseData = dataBankService.retrieveData(planetId, dataBank);

        if (responseData == null) {
            // No data means no consent.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No consent");
        } else {
            return ResponseEntity.ok(responseData);
        }
    }

    @PostMapping("/{dataBank}/consent")
    public AuthRequest createOpenIdContext(@AuthenticationPrincipal UserInfo userInfo,
                                           @RequestParam(required = false, value = "lang") String language,
                                           @PathVariable String dataBank,
                                           HttpServletResponse response) {

        String planetId = userInfo.getPlanetId();
        String consentDocument = consentContainerService.createConsentDocumentForProvider(dataBank, planetId);
        String redirectUri = appProperties.getBaseUrl() + "/api/callback/consent";
        return openIdSupport.createRequestForConsent(response, redirectUri, planetId, consentDocument);
    }
}
