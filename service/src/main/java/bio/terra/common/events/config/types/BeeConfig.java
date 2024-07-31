package bio.terra.common.events.config.types;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "terra.common.bee")
public record BeeConfig(String name, Boolean isActive) {}
