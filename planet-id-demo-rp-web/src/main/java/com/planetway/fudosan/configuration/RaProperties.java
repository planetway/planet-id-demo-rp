package com.planetway.fudosan.configuration;

import com.planetway.fudosan.domain.PlanetXSubsystem;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "ra")
@Validated
@Data
public class RaProperties {

    @NotBlank
    private String pcoreRpId;

    @NotNull
    private PlanetXSubsystem planetxSubsystem;

    @NotBlank
    private String personInfoServiceCode;
}
