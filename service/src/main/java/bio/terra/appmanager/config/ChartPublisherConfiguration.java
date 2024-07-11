package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.publisher.chart")
public record ChartPublisherConfiguration(String topicId, String projectId) {}
