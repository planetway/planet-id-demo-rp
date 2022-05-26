package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.LoginRequest;
import com.planetway.rp.configuration.PCoreProperties;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RequiredArgsConstructor
@Controller("htmlAppController")
@RequestMapping()
public class AppController {

    private static final String LOGIN_USER = "loginUser";

    private final OpenIdSupport openIdSupport;
    private final AppProperties appProperties;
    private final PCoreProperties pCoreProperties;

    @GetMapping("/")
    public String index() {
        return "redirect:/property";
    }

    @GetMapping("login")
    public ModelAndView login(Model model, HttpServletResponse response) {
        model.addAttribute(LOGIN_USER, new LoginRequest());
        String redirectUri = appProperties.getBaseUrl() + "/callback/login";
        AuthRequest authRequest = openIdSupport.createRequestForAuthenticate(response, redirectUri);
        model.addAttribute("authRequest", authRequest);
        return new ModelAndView("login");
    }

    @PostMapping("logout")
    public ModelAndView logout() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Logout user: {}", currentUserName);
        SecurityContextHolder.clearContext();
        return new ModelAndView("index");
    }

    @GetMapping("settings")
    public ModelAndView settings(HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("settings");
        String redirectUri = appProperties.getBaseUrl() + "/callback/linking";
        AuthRequest authRequest = openIdSupport.createRequestForAuthenticate(response, redirectUri);
        modelAndView.addObject("authRequest", authRequest);
        return modelAndView;
    }

    @PostMapping("redirect-to-pcore")
    public void loginPlanetId(HttpServletRequest request, HttpServletResponse response) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(pCoreProperties.getUrl())
                .path("/v2/openid/auth");
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.addHeader("Location", uriComponentsBuilder.toUriString());
    }
}
