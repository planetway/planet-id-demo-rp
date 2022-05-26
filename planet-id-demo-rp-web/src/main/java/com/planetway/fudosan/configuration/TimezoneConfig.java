package com.planetway.fudosan.configuration;

import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimezoneConfig {

    public TimezoneConfig(AppProperties appProperties) {
        // Set the application timezone with the system timezone not being affected.
        TimeZone.setDefault(TimeZone.getTimeZone(appProperties.getTimezone()));
    }
}
