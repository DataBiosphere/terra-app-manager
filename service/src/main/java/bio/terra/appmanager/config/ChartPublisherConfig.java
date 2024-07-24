package bio.terra.appmanager.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ChartPublisherConfig implements GooglePublisherConfiguration {

  PubsubGoogleConfig googleConfig;
  PubsubBeeConfig beeConfig;

  public ChartPublisherConfig(PubsubGoogleConfig googleConfig, PubsubBeeConfig beeConfig) {
    this.googleConfig = googleConfig;
    this.beeConfig = beeConfig;
  }

  @Override
  public String getBaseName() {
    return "event-charts";
  }

  @Override
  public String getTopicId() {
    if (beeConfig.isActive()) {
      return getBaseName() + "-" + beeConfig.name();
    } else {
      return getBaseName();
    }
  }
}
