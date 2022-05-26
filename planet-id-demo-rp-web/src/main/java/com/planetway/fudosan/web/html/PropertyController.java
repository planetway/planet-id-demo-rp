package com.planetway.fudosan.web.html;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.SignedDocumentInfo;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.service.DataBankService;
import com.planetway.fudosan.service.DocumentContainerService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.service.PCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@RequiredArgsConstructor
@Controller
@RequestMapping("property")
public class PropertyController {

    private static final String PROPERTY_INDEX_HTML = "property";

    private final AppProperties appProperties;
    private final PCoreService pCoreService;
    private final OpenIdSupport openIdSupport;
    private final DataBankService dataBankService;
    private final DocumentContainerService documentContainerService;

    @GetMapping
    public String get() {
        return PROPERTY_INDEX_HTML;
    }

    @GetMapping("contract-review")
    public Object getContractReviewPage(@AuthenticationPrincipal UserInfo userInfo,
                                        @RequestParam String dataBankName,
                                        HttpServletResponse response) {
        Map<String, String> responseData = dataBankService.retrieveData(userInfo.getPlanetId(), dataBankName);
        if (responseData == null) {
            // no consent, will not handle further
            return "redirect:/error";
        }

        String language = getLocale().getLanguage().replace("ja", "jp");
        Map<String, String> payload = new HashMap<>(6);
        payload.put("name", responseData.get("name_" + language));
        payload.put("dob", responseData.get("dob_" + language));
        payload.put("address", responseData.get("address_" + language));
        payload.put("phone", responseData.get("phone"));
        payload.put("bank_account", responseData.get("bank_account"));
        payload.put("employer", responseData.get("employer_" + language));

        return createReviewModelAndView(userInfo, payload, response);
    }

    @PostMapping("contract-review")
    public ModelAndView postContractReviewPage(@AuthenticationPrincipal UserInfo userInfo,
                                               HttpServletResponse response,
                                               @RequestBody MultiValueMap<String, String> payload) {
        // POST is done when there is already existing consent
        return createReviewModelAndView(userInfo, payload.toSingleValueMap(), response);
    }

    @GetMapping("contract-success")
    public String getContractSuccessPage() {
        return "property-contract-success";
    }

    private ModelAndView createReviewModelAndView(UserInfo userInfo, Map<String, String> payload, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("property-contract-review");
        modelAndView.addAllObjects(payload);

        String file = documentContainerService.createFileSignContainer();
        String redirectUri = appProperties.getBaseUrl() + "/callback/signing";
        AuthRequest authRequest = openIdSupport.createRequestForSign(response, redirectUri, userInfo.getPlanetId(), file);
        modelAndView.addObject("authRequest", authRequest);

        return modelAndView;
    }
}
