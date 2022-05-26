package com.planetway.fudosan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.configuration.RaProperties;
import com.planetway.fudosan.domain.ErrorResponse;
import com.planetway.fudosan.domain.Person;
import com.planetway.fudosan.exception.NoConsentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@Service
@Slf4j
public class RaService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final String personInfoUrl;

    public RaService(AppProperties appProperties, RaProperties raProperties, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;

        personInfoUrl = appProperties.getPlanetXSecurityServerUrl()
                + "/r1/"
                + raProperties.getPlanetxSubsystem().toString()
                + "/"
                + raProperties.getPersonInfoServiceCode();
    }

    public Person getPerson(String planetId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Road-Client", appProperties.getPlanetXSubsystem().toString());
        headers.set("X-Road-TargetUserId", planetId);

        HttpEntity entity = new HttpEntity(headers);

        try {
            return restTemplate.exchange(personInfoUrl, GET, entity, Person.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            String errorString = e.getResponseBodyAsString();

            log.error("getPerson failed: " + errorString);

            ErrorResponse error = null;
            try {
                error = objectMapper.readValue(errorString, ErrorResponse.class);
            } catch (Exception e2) {
                log.error("Failed to deserialize service exception: " + errorString, e2);
            }

            if (error != null && "NO_CONSENT".equals(error.getErrorCode())) {
                throw new NoConsentException();
            }

            throw e;
        }
    }
}
