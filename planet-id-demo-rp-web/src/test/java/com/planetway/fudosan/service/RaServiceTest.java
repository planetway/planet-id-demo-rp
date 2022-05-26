package com.planetway.fudosan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.configuration.RaProperties;
import com.planetway.fudosan.domain.ErrorResponse;
import com.planetway.fudosan.domain.Person;
import com.planetway.fudosan.domain.PlanetXSubsystem;
import com.planetway.fudosan.exception.NoConsentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaServiceTest {

    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private RaService raService;

    @BeforeEach
    public void setup() {
        AppProperties appProperties = new AppProperties();
        appProperties.setPlanetXSecurityServerUrl("ss");
        appProperties.setPlanetXSubsystem(new PlanetXSubsystem("a/b/c/d"));

        RaProperties raProperties = new RaProperties();
        raProperties.setPlanetxSubsystem(new PlanetXSubsystem("a/b/c/d"));

        objectMapper = new ObjectMapper();
        restTemplate = mock(RestTemplate.class);
        raService = new RaService(
                appProperties,
                raProperties,
                objectMapper,
                restTemplate
        );
    }

    @Test
    public void raService_throwsNoConsent() throws JsonProcessingException {
        ErrorResponse expectedError = new ErrorResponse("NO_CONSENT", "");
        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<Class<Person>>any()))
                .thenThrow(new RestClientResponseException(
                        "", 403, "", null, objectMapper.writeValueAsString(expectedError).getBytes(), null
                ));

        assertThatThrownBy(() -> raService.getPerson("planet-id"))
                .isInstanceOf(NoConsentException.class);
    }

    @Test
    public void raService_throws() throws JsonProcessingException {
        ErrorResponse expectedError = new ErrorResponse("", "");
        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<Class<Person>>any()))
                .thenThrow(new RestClientResponseException(
                        "", 400, "", null, objectMapper.writeValueAsString(expectedError).getBytes(), null
                ));

        assertThatThrownBy(() -> raService.getPerson("planet-id"))
                .isInstanceOf(RestClientResponseException.class);
    }

    @Test
    public void raService_success() {
        Person person = new Person();

        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<Class<Person>>any()))
                .thenReturn(new ResponseEntity<>(person, HttpStatus.OK));

        assertThat(raService.getPerson("planet-id")).isEqualTo(person);
    }
}
