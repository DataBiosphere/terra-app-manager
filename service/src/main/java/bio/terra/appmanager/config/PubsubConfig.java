package bio.terra.appmanager.config;

import bio.terra.appmanager.dao.PublisherDao;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubsubConfig {

  @Bean
  @Autowired
  public PublisherDao chartPublisherDao(ChartPublisherConfig config) {
    TopicName topicName = TopicName.of(config.getTopicId(), config.getProjectId());
    Publisher publisher;
    try {
      publisher = Publisher.newBuilder(topicName).build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new PublisherDao(publisher);
  }
}
