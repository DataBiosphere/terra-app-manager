package bio.terra.common.events.client.google;

import bio.terra.common.events.client.PubsubClient;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.naming.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for interacting with the Google PubSub infrastructure and handling both
 * publishing to topics and subscribing from topics.
 */
public class GooglePubsubClient extends PubsubClient {

  private static final Logger logger = LoggerFactory.getLogger(GooglePubsubClient.class);

  private String projectId;
  private String topicName;
  private Publisher publisher;

  public GooglePubsubClient(String projectId, String topicName, boolean createTopic) {
    this.projectId = projectId;
    this.topicName = topicName;
    publisher = buildPublisher(projectId, topicName, createTopic);
  }

  @Override
  public void publish(byte[] message) {
    if (logger.isDebugEnabled()) {
      logger.debug(new String(message, StandardCharsets.UTF_8));
    }
    // TODO: remove me
    logger.info(new String(message, StandardCharsets.UTF_8));
  }

  @Override
  public void subscribe() {}

  @Override
  public void close() throws IOException {}

  private Publisher buildPublisher(String projectId, String topicName, boolean createTopic) {
    try {
      TopicName topic = verifyTopic(projectId, topicName, createTopic);
      return Publisher.newBuilder(topic).build();
    } catch (IOException | ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private TopicName verifyTopic(String projectId, String topicName, boolean createTopic)
      throws IOException, ConfigurationException {
    EventTopicName topicCreator = null;
    if (createTopic) {
      topicCreator = new EventTopicMustBeAlreadyCreated(projectId);
    } else {
      topicCreator = new CreateEventTopicIfNotExist(projectId);
    }
    return topicCreator.verifyTopicName(topicName);
  }
}
