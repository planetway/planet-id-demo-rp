package com.planetway.fudosan.domain;

import lombok.Data;

@Data
public class Consent {
    private String clientCode;
    private String providerCode;
    private String serviceCode;
    private String targetUserId;
}
