package bio.terra.common.events.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "terra.common.pubsub.google")
public record PubsubGoogleConfig(String projectId) {}
