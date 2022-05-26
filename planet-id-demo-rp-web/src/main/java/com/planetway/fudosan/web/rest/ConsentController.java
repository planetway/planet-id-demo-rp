package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.ConsentContainerService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/consent")
@RestController
public class ConsentController {

    private final ConsentContainerService consentContainerService;

    public ConsentController(ConsentContainerService consentContainerService) {
        this.consentContainerService = consentContainerService;
    }

    @GetMapping(value = "/consent-container/{dataBankName}", produces = MediaType.TEXT_XML_VALUE)
    public String createConsentContainer(@PathVariable String dataBankName) {
        UserInfo principal = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return consentContainerService.createConsentDocumentForProvider(dataBankName, principal.getPlanetId());
    }
}
