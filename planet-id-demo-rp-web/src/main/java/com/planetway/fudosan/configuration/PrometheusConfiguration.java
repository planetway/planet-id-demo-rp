package com.planetway.fudosan.configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthEndpointGroups;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.NamedContributors;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
public class PrometheusConfiguration {

    public PrometheusConfiguration(MeterRegistry meterRegistry, HealthContributorRegistry healthContributorRegistry, HealthEndpointGroups groups, HealthEndpoint healthEndpoint) {
        meterRegistry.gauge("health", healthEndpoint, he -> he.health().getStatus() == Status.UP ? 1 : 0);

        registerIndicator(meterRegistry, healthContributorRegistry, new ArrayList<>());
    }

    private static void registerIndicator(MeterRegistry meterRegistry, NamedContributors<HealthContributor> namedContributors, List<String> group) {
        for (NamedContributor<HealthContributor> contributor : namedContributors) {
            String name = contributor.getName();
            HealthContributor healthContributor = contributor.getContributor();
            if (healthContributor instanceof HealthIndicator) {
                String compositeName = StringUtils.join(group, "_") + (group.isEmpty() ? "" : "_") + name;

                log.info("Registering {} health indicator.", compositeName);

                Gauge.builder("health_" + compositeName, (HealthIndicator) healthContributor, PrometheusConfiguration::getHealthValueAsDouble)
                        .strongReference(true) // Otherwise some indicators may be GCd as they can be instantiated on demand and micrometer keeps weak refs by default.
                        .register(meterRegistry);
            } else if (healthContributor instanceof CompositeHealthContributor) {
                group = new ArrayList<>(group);
                group.add(name);
                registerIndicator(meterRegistry, (CompositeHealthContributor) healthContributor, group);
            }
        }
    }

    private static double getHealthValueAsDouble(HealthIndicator healthIndicator) {
        return healthIndicator.health().getStatus() == Status.UP ? 1 : 0;
    }
}
