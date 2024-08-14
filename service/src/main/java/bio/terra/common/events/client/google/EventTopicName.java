package bio.terra.common.events.client.google;

import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;

public interface EventTopicName {
  TopicName verifyTopicName(String name) throws IOException, ConfigurationException;
}
