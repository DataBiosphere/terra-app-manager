package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.sam")
public record SamConfiguration(String basePath) {}
