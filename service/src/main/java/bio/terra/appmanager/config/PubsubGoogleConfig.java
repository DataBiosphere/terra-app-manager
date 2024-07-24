package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.pubsub.google")
public record PubsubGoogleConfig(String projectId) {}
