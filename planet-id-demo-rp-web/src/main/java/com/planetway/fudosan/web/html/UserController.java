package com.planetway.fudosan.web.html;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.SignUpForm;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.domain.ValidationResult;
import com.planetway.fudosan.service.UserService;
import com.planetway.rp.oauth.OpenIdSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller("htmlUserController")
@RequestMapping("user")
public class UserController {

    private static final String SIGN_UP_FORM = "signUpForm";
    private static final String SIGN_UP = "user-sign-up";
    private static final String SIGN_UP_SUCCESS = "user-sign-up-success";

    private final UserService userService;
    private final OpenIdSupport openIdSupport;
    private final AppProperties appProperties;

    @GetMapping("sign-up")
    public ModelAndView signUp(Model model) {
        model.addAttribute(SIGN_UP_FORM, new SignUpForm());
        return new ModelAndView(SIGN_UP);
    }

    @PostMapping("sign-up")
    public ModelAndView create(@ModelAttribute(SIGN_UP_FORM) SignUpForm form, BindingResult bindingResult) {
        ValidationResult validationResult = userService.create(form);
        if (validationResult.hasErrors()) {
            validationResult.getErrors().forEach(error -> bindingResult.addError(new ObjectError(SIGN_UP_FORM, error.getCode())));
            return new ModelAndView(SIGN_UP);
        }
        return new ModelAndView(SIGN_UP_SUCCESS);
    }

    @PostMapping("unlink")
    public String unlinkPlanetId(@AuthenticationPrincipal UserInfo userInfo) {
        userService.unlinkPlanetId(userInfo);
        return "redirect:/";
    }

    @PostMapping("delete")
    public String delete(@AuthenticationPrincipal UserInfo userInfo) {
        userService.delete(userInfo);
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}
