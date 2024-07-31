package bio.terra.appmanager.config;

import bio.terra.common.events.client.google.GooglePublisherConfiguration;
import bio.terra.common.events.config.types.BeeConfig;
import bio.terra.common.events.config.types.GoogleConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChartPublisherConfig implements GooglePublisherConfiguration {

  GoogleConfig googleConfig;
  BeeConfig beeConfig;

  public ChartPublisherConfig(GoogleConfig googleConfig, BeeConfig beeConfig) {
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
