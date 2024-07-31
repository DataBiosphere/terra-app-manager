package bio.terra.common.events.config.types;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "terra.common.google")
public record GoogleConfig(String projectId) {}
