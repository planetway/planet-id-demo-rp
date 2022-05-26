package com.planetway.fudosan.domain;

import lombok.Data;

@Data
public class Address {

    private String countryCode;
    private String postalCode;
    private String buildingApartment;
    private String buildingApartmentIntl;
    private String street;
    private String streetIntl;
    private String city;
    private String cityIntl;
    private String prefecture;
    private String prefectureIntl;
}
