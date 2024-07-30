package bio.terra.appmanager.config;

import bio.terra.common.events.config.GooglePublisherConfiguration;
import bio.terra.common.events.config.PubsubBeeConfig;
import bio.terra.common.events.config.PubsubGoogleConfig;
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
    System.out.println("name:      " + beeConfig.name());
    System.out.println("is_active: " + beeConfig.isActive());

    if (beeConfig.isActive()) {
      return getBaseName() + "-" + beeConfig.name();
    } else {
      return getBaseName();
    }
  }
}
