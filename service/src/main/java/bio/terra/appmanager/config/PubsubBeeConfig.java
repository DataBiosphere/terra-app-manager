package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.pubsub.bee")
public record PubsubBeeConfig(String name, Boolean isActive) {}
