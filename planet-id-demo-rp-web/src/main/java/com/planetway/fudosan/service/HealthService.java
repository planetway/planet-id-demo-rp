package com.planetway.fudosan.service;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.planetx.PlanetXSoapTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class HealthService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final PlanetXSoapTemplate planetXSoapTemplate;

    public HealthService(AppProperties appProperties, RestTemplate restTemplate, PlanetXSoapTemplate planetXSoapTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
        this.planetXSoapTemplate = planetXSoapTemplate;
    }

    public String getSecurityServerConnectorServiceHealth() {
        log.info("Getting health from " + appProperties.getPlanetxConnectorHealthService());
        String result = planetXSoapTemplate.execute(appProperties.getPlanetxConnectorHealthService());

        log.info("Got health '{}'", result);

        return result;
    }
}
