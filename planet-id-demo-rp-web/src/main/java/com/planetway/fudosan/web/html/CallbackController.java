package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.SignedDocumentEntity;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.repository.SignedDocumentRepository;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.AuthResponse;
import com.planetway.rp.oauth.OpenIdSupport;
import com.planetway.rp.oauth.TokenResponse;
import com.planetway.rp.service.PCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Controller("htmlCallbackController")
@RequestMapping("callback")
public class CallbackController {
    private final AppProperties appProperties;
    private final OpenIdSupport openIdSupport;
    private final UserService userService;
    private final SignedDocumentRepository signedDocumentRepository;

    @GetMapping("login")
    public String loginCallback(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam String code,
                                @RequestParam String state) {
        TokenResponse tokenResponse = handleCallback(null, request, response, code, state, "/callback/login");
        userService.loginUserWithPlanetId(tokenResponse.getPlanetId());
        return "redirect:/";
    }

    @GetMapping("linking")
    public String linkingCallback(@AuthenticationPrincipal UserInfo userInfo,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam String code,
                                  @RequestParam String state) {
        TokenResponse tokenResponse = handleCallback(userInfo, request, response, code, state, "/callback/linking");

        try {
            userService.linkPlanetId(userInfo, tokenResponse.getPlanetId());
        } catch (Exception e) {
            return "redirect:/callback/error-linked/" + tokenResponse.getPlanetId();
        }

        return "redirect:/settings";
    }

    @GetMapping("error-linked/{planetId}")
    public ModelAndView getErrorPage(@PathVariable(name = "planetId") String planetId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-linked");
        modelAndView.addObject("planetId", planetId);
        return modelAndView;
    }

    @GetMapping("signing")
    public String signingCallback(@AuthenticationPrincipal UserInfo userInfo,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam String code,
                                  @RequestParam String state) {
        // this callback is shared between getting info from data banks and LRA
        try {
            handleCallback(userInfo, request, response, code, state, "/callback/signing");
        } catch (Exception e) {
            log.error("Could not exchange code: {}", e.getMessage());
            return "redirect:/error";
        }
        return "redirect:/property/contract-success";
    }

    @GetMapping("consent")
    public String consentCallback(@AuthenticationPrincipal UserInfo userInfo,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam String code,
                                  @RequestParam String state) {
        // this callback is shared between getting info from data banks and LRA
        try {
            handleCallback(userInfo, request, response, code, state, "/callback/consent");
        } catch (Exception e) {
            log.error("Could not exchange code: {}", e.getMessage());
            return "redirect:/error";
        }

        if (openIdSupport.isValidCookie(request, response, "get_person_info", "1")) {
            // callback after getting consent to get personal info from RA
            return "redirect:/lra/person";
        } else {
            String dataBank = Arrays.stream(request.getCookies())
                    .filter(cookie -> "dataBankName".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            return "redirect:/property/contract-review?dataBankName=" + dataBank;
        }
    }

    private TokenResponse handleCallback(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, String code, String state, String redirectPath) {
        String redirectUri = appProperties.getBaseUrl() + redirectPath;

        AuthResponse authorization = new AuthResponse();
        authorization.setCode(code);
        authorization.setState(state);
        authorization.setCallback(redirectUri);

        TokenResponse tokenResponse = openIdSupport.handleCallback(request, response, authorization);

        if ("/callback/signing".equals(redirectPath) || "/callback/consent".equals(redirectPath)) {
            // in case of signing or consent, save the document to database
            SignedDocumentEntity doc = new SignedDocumentEntity();
            doc.setSignatureType("/callback/signing".equals(redirectPath) ? "SIGNING" : "CONSENT");
            doc.setUserId(userInfo.getId());
            doc.setPlanetId(userInfo.getPlanetId());
            doc.setData(tokenResponse.getSignedContainer());
            doc.setUuid(tokenResponse.getPayloadUuid());
            doc.setHasTimestamp(tokenResponse.isSignedContainerTimestamped());
            signedDocumentRepository.save(doc);
        }

        return tokenResponse;
    }
}
