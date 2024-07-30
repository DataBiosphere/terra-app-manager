package bio.terra.common.events.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "terra.common.pubsub.bee")
public record PubsubBeeConfig(String name, Boolean isActive) {}
