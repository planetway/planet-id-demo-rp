package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/security-server-connector-service")
    public String securityServerConnectorService(HttpServletRequest request) {
        return healthService.getSecurityServerConnectorServiceHealth();
    }
}
