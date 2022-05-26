package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.SignUpForm;
import com.planetway.fudosan.domain.UserCredentials;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.domain.ValidationResult;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.AuthRequest;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AppProperties appProperties;
    private final OpenIdSupport openIdSupport;
    private final UserService userService;

    public UserController(AppProperties appProperties, OpenIdSupport openIdSupport, UserService userService) {
        this.appProperties = appProperties;
        this.openIdSupport = openIdSupport;
        this.userService = userService;
    }

    @GetMapping
    public UserInfo get(@AuthenticationPrincipal UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody UserCredentials userCredentials) {
        ValidationResult validationResult = userService.create(new SignUpForm(
                userCredentials.getUser(),
                userCredentials.getPassword(),
                userCredentials.getPassword()
        ));

        if (!validationResult.hasErrors()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(validationResult);
        }
    }

    @DeleteMapping
    public String delete(@AuthenticationPrincipal UserInfo userInfo) {
        userService.delete(userInfo);

        SecurityContextHolder.clearContext();

        return "\"OK\"";
    }

    @PostMapping("link")
    public AuthRequest linkWithPlanetId(
            HttpServletResponse response,
            @RequestParam(required = false, value = "lang") String language) {
        String redirectUri = appProperties.getBaseUrl() + "/api/callback/link";
        return openIdSupport.createRequestForAuthenticate(response, redirectUri);
    }

    @PostMapping("unlink")
    public UserInfo unlinkPlanetId(@AuthenticationPrincipal UserInfo userInfo) {
        return userService.unlinkPlanetId(userInfo);
    }
}
