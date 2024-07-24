package bio.terra.appmanager.config;

import bio.terra.appmanager.dao.EventTopicName;
import bio.terra.appmanager.dao.TopicCreatorFactory;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubsubConfig {

  @Bean
  public EventTopicName getEventTopicName(ChartPublisherConfig config, PubsubBeeConfig beeConfig) {
    if (beeConfig.isActive()) {
      return TopicCreatorFactory.createCreateEventTopicIfNotExist(config.getTopicId());
    } else {
      return TopicCreatorFactory.createEventTopicMustBeAlreadyCreated(config.getTopicId());
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
