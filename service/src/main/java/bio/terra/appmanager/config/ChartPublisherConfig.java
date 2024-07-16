package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.publisher.chart")
public record ChartPublisherConfig(String topicId, String projectId, String environment)
    implements GooglePublisherConfiguration {
  @Override
  public String getTopicId() {
    return topicId;
  }

  @Override
  public String getProjectId() {
    return projectId;
  }

  @Override
  public String getEnvironment() {
    return environment;
  }
}
