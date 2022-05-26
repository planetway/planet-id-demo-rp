package com.planetway.fudosan.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomInfoContributor implements InfoContributor {

    private final ApplicationContext context;

    public CustomInfoContributor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void contribute(Info.Builder builder) {
        try {
            BuildProperties buildProperties = context.getBean(BuildProperties.class);
            builder.withDetail("buildtime", buildProperties.getTime().toString());
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("BuildProperties bean missing, cannot publish build information. If running from IntellJ, you could turn on option 'Delegate IDE build/run actions to gradle' to get build information back.");
        }
    }
}
