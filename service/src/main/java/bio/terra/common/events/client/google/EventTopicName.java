package bio.terra.common.events.client.google;

import bio.terra.appmanager.config.ChartPublisherConfig;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;

public interface EventTopicName {
  TopicName getEventTopicName(ChartPublisherConfig config)
      throws IOException, ConfigurationException;
}
