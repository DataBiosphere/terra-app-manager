package bio.terra.common.events.client.google;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTopicMustBeAlreadyCreated extends EventTopicName {
  private static final Logger logger =
      LoggerFactory.getLogger(EventTopicMustBeAlreadyCreated.class);

  private final String projectId;

  public EventTopicMustBeAlreadyCreated(
      String projectId, boolean connectLocal, String emulatorTargetUrl) {
    super(connectLocal, emulatorTargetUrl);
    this.projectId = projectId;
  }

  /**
   * This is called when running in the Production environment Verify the topic exists or generate a
   * ConfigurationError then return the TopicName
   *
   * @param name
   * @return TopicName for the Event topic for Production
   */
  @Override
  public TopicName verifyTopicName(String name) throws ConfigurationException, IOException {

    try (TopicAdminClient topicAdminClient = buildTopicAdminClient()) {
      TopicName topicName = TopicName.of(projectId, name);
      Topic topic = topicAdminClient.getTopic(topicName);
      if (topic != null) {
        return topicName;
      }
      throw new ConfigurationException("Error, Event Topic " + topicName + " must exist");
    } catch (Exception e) {
      logger.error("Error getting Event Topic with topic id: " + name + " " + e.getMessage());
      throw e;
    }
  }
}
