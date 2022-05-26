package com.planetway.fudosan.domain;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ConsentContainerRequest {
    private String signRequestType;
    private OffsetDateTime validTill;
    private String requestURI;
    private Boolean revokable;

    private String planetId;
    private String dataService;

    private String dataProviderRelyingPartyCode;
    private String dataProviderPlanetXCode;

    private String dataConsumerRelyingPartyCode;
    private String dataConsumerPlanetXCode;

    private String memberClass;
    private String memberCode;
    private String subsystemCode;
}
