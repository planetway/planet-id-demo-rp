package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RequiredArgsConstructor
@Controller("htmlDataBankController")
@RequestMapping("data-bank")
public class DataBankController {
    private final AppProperties appProperties;
    private final OpenIdSupport openIdSupport;

    @GetMapping("select")
    public ModelAndView getConsentRequest(@AuthenticationPrincipal UserInfo userInfo,
                                          HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("data-bank-select");
        String redirectUri = appProperties.getBaseUrl() + "/callback/consent";
        AuthRequest authRequest = openIdSupport.createRequestForConsent(response, redirectUri, userInfo.getPlanetId(), null);
        modelAndView.addObject("authRequest", authRequest);
        modelAndView.addObject("dataBankNames", appProperties.getDataBanks().keySet());
        return modelAndView;
    }
}
