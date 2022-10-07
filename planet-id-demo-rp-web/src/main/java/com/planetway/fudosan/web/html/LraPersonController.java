package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.Person;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.exception.NoConsentException;
import com.planetway.fudosan.exception.PlanetIdNotLinkedException;
import com.planetway.fudosan.service.ConsentContainerService;
import com.planetway.fudosan.service.RaService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LraPersonController {

    private final AppProperties appProperties;
    private final RaService raService;
    private final ConsentContainerService consentContainerService;
    private final OpenIdSupport openIdSupport;

    @GetMapping("/lra/person")
    public ModelAndView getVerifiedPersonPage(HttpServletResponse response) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isBlank(userInfo.getPlanetId())) {
            throw new PlanetIdNotLinkedException("");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("lra-person");

        try {
            Person person = raService.getPerson(userInfo.getPlanetId());
            modelAndView.addObject("person", person);
            modelAndView.addObject("address", person.getAddress()
                    .stream().findFirst().orElse(null));
        } catch (NoConsentException e) {
            // Build consent document for RA and redirect user to sign it.
            String redirectUri = appProperties.getBaseUrl() + "/callback/consent";
            UUID consentId = UUID.randomUUID();
            String consentDocumentPayload = consentContainerService.createConsentContainerForRa(userInfo.getPlanetId(), consentId);
            AuthRequest authRequest = openIdSupport.createRequestForConsent(response, redirectUri, userInfo.getPlanetId(), consentDocumentPayload);
            response.addCookie(new Cookie("get_person_info", "1"));
            response.addCookie(new Cookie("consent_uuid", consentId.toString()));

            modelAndView.addObject("authRequest", authRequest);
        } catch (RuntimeException e) {
            log.error("Error: " + e);
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }
}
