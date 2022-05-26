package com.planetway.fudosan.web;

import com.planetway.fudosan.exception.PlanetIdNotLinkedException;
import com.planetway.fudosan.web.html.AppController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice(basePackageClasses = AppController.class)
public class HtmlExceptionHandler {

    @ExceptionHandler(PlanetIdNotLinkedException.class)
    public RedirectView handleMyException(PlanetIdNotLinkedException ex) {
        return new RedirectView("/login?linkError");
    }
}
