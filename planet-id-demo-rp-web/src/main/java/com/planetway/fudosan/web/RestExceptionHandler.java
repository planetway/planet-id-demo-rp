package com.planetway.fudosan.web;

import com.planetway.fudosan.domain.ApiError;
import com.planetway.fudosan.exception.NoConsentException;
import com.planetway.fudosan.exception.PlanetIdNotLinkedException;
import com.planetway.fudosan.web.rest.AppController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(basePackageClasses = AppController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        log.error("Exception", ex);
        return ResponseEntity.badRequest().body("");
    }

    @ExceptionHandler(PlanetIdNotLinkedException.class)
    public final ResponseEntity<Object> handleUserNotLinkedWithPlanetIdException(PlanetIdNotLinkedException ex) {
        log.error("Exception", ex);
        return ResponseEntity.badRequest().body(new ApiError("PLANETID_NOT_LINKED", "User not linked with a planet id"));
    }

    @ExceptionHandler(NoConsentException.class)
    public final ResponseEntity<Object> handleNoConsent(NoConsentException ex) {
        log.error("Exception", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("NO_CONSENT", "No consent"));
    }
}
