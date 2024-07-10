package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.publisher")
public record PublisherConfiguration(String topicId, String projectId) {}
