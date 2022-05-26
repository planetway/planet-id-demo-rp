package com.planetway.fudosan.configuration;

import com.planetway.fudosan.domain.DataBank;
import com.planetway.fudosan.domain.PlanetXService;
import com.planetway.fudosan.domain.PlanetXSubsystem;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppProperties {

    @NotBlank
    private String baseUrl;
    private String timezone;

    @NotBlank
    private String planetXSecurityServerUrl;

    @NotNull
    private PlanetXSubsystem planetXSubsystem;

    @NotNull
    private PlanetXService planetxConnectorHealthService;

    @NotNull
    private Map<String, DataBank> dataBanks;
}
