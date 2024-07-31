package bio.terra.common.events.config;

import bio.terra.appmanager.config.ChartPublisherConfig;
import bio.terra.common.events.client.google.EventTopicName;
import bio.terra.common.events.client.google.TopicCreatorFactory;
import bio.terra.common.events.config.types.BeeConfig;
import bio.terra.common.events.config.types.GoogleConfig;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Based on all the known pubsub configurations, present the logic necessary for the client to
 * connect to the necessary resources.
 */
@Configuration
public class PubsubConfig {

  private String applicationName;

  BeeConfig beeConfig;
  GoogleConfig googleConfig;

  public PubsubConfig(
      ApplicationContext applicationContext, BeeConfig beeConfig, GoogleConfig googleConfig) {
    this.applicationName = applicationContext.getId();
    this.beeConfig = beeConfig;
    this.googleConfig = googleConfig;
  }

  @Bean(name = "eventTopicName")
  public EventTopicName getEventTopicName() {

    System.out.println("name:             " + beeConfig.name());
    System.out.println("is_active:        " + beeConfig.isActive());
    System.out.println("application_name: " + applicationName);

    if (beeConfig.isActive()) {
      return TopicCreatorFactory.createCreateEventTopicIfNotExist(googleConfig.projectId());
    } else {
      return TopicCreatorFactory.createEventTopicMustBeAlreadyCreated(googleConfig.projectId());
    }
  }

  @Bean
  @Autowired
  public Publisher chartPublisherDao(ChartPublisherConfig config, EventTopicName eventTopicName) {
    Publisher publisher;
    try {
      TopicName topicName = eventTopicName.getEventTopicName(config);
      publisher = Publisher.newBuilder(topicName).build();
    } catch (IOException | ConfigurationException e) {
      throw new RuntimeException(e);
    }
    return publisher;
  }
}
