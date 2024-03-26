package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.status-check")
public record StatusCheckConfiguration(
    boolean enabled,
    int pollingIntervalSeconds,
    int startupWaitSeconds,
    int stalenessThresholdSeconds) {}
