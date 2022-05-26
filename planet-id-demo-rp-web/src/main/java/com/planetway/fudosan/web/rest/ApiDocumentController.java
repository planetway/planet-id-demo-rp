package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.DocumentContainerService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;


@RequestMapping("/api/document")
@RestController
public class ApiDocumentController {
    private final AppProperties appProperties;
    private final DocumentContainerService documentContainerService;
    private final OpenIdSupport openIdSupport;

    public ApiDocumentController(AppProperties appProperties, DocumentContainerService documentContainerService, OpenIdSupport openIdSupport) {
        this.appProperties = appProperties;
        this.documentContainerService = documentContainerService;
        this.openIdSupport = openIdSupport;
    }

    @PostMapping("sign")
    public AuthRequest createOpenIdContext(@AuthenticationPrincipal UserInfo userInfo,
                                           @RequestParam MultipartFile file,
                                           @RequestParam(required = false, value = "lang") String language,
                                           HttpServletResponse response) {

        String redirectUri = appProperties.getBaseUrl() + "/api/callback/sign";
        String planetId = userInfo.getPlanetId();
        String fileContainer = documentContainerService.createFileSignContainer(file);
        return openIdSupport.createRequestForSign(response, redirectUri, planetId, fileContainer);
    }
}
