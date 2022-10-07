package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.SignedDocumentEntity;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.repository.SignedDocumentRepository;
import com.planetway.fudosan.service.ConsentContainerService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.service.PCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller("htmlSignedDocumentsController")
@RequestMapping("/signed-documents")
public class SignedDocumentsController {
    private final SignedDocumentRepository signedDocumentRepository;
    private final AppProperties appProperties;
    private final PCoreService pCoreService;
    private final OpenIdSupport openIdSupport;
    private final ConsentContainerService consentContainerService;

    @GetMapping("")
    public ModelAndView getSignedDocuments(@AuthenticationPrincipal UserInfo userInfo) {
        ModelAndView modelAndView = new ModelAndView("signed-document-list");
        List<SignedDocumentEntity> docs = signedDocumentRepository.getAllByUserId(userInfo.getId());
        modelAndView.addObject("docs", docs);

        return modelAndView;
    }

    @GetMapping("{uuid}/refresh")
    public ModelAndView refresh(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid) {
        byte[] doc = pCoreService.getSignedContainer(uuid);
        if (pCoreService.isAsiceContainerTimestamped(doc)) {
            SignedDocumentEntity dbDoc = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);
            dbDoc.setData(doc);
            dbDoc.setHasTimestamp(true);
            signedDocumentRepository.save(dbDoc);
        }
        return getSignedDocuments(userInfo);
    }

    @GetMapping("{uuid}/download")
    public ResponseEntity<byte[]> download(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid) {

        SignedDocumentEntity doc = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + uuid + ".asice");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        byte[] asice = doc.getData();
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(asice.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(asice);
    }

    @GetMapping("{uuid}/revoke")
    public ResponseEntity<String> revokeConsent(@AuthenticationPrincipal UserInfo userInfo, @PathVariable String uuid, HttpServletResponse response) {
        String redirectUri = appProperties.getBaseUrl() + "/callback/consent-revoke";

        SignedDocumentEntity sde = signedDocumentRepository.findByUserIdAndUuid(userInfo.getId(), uuid);
        String redirectUrl = "/signed-documents";
        if (userInfo.getPlanetId().equals(sde.getPlanetId())) {
            String consentRevokeDocument = consentContainerService.createConsentRevokeDocument(sde.getConsentUuid(), userInfo.getPlanetId());
            AuthRequest authRequest = openIdSupport.createRequestForConsentRevoke(response, redirectUri, userInfo.getPlanetId(), consentRevokeDocument);
            redirectUrl = authRequest.toLocation();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
