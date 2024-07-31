package bio.terra.common.events.client.google;

import bio.terra.appmanager.config.ChartPublisherConfig;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTopicMustBeAlreadyCreated implements EventTopicName {
  private static final Logger logger =
      LoggerFactory.getLogger(EventTopicMustBeAlreadyCreated.class);

  private final String projectId;

  public EventTopicMustBeAlreadyCreated(String projectId) {
    this.projectId = projectId;
  }

  /**
   * This is called when running in the Production environment Verify the topic exists or generate a
   * ConfigurationError # Then return the TopicName
   *
   * @param config
   * @return TopicName for the Event topic for Production
   */
  @Override
  public TopicName getEventTopicName(ChartPublisherConfig config)
      throws ConfigurationException, IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      TopicName topicName = TopicName.of(projectId, config.getTopicId());
      Topic topic = topicAdminClient.getTopic(topicName);
      if (topic != null) {
        return topicName;
      }
      throw new ConfigurationException("Error, Event Topic " + topicName + " must exist");
    } catch (Exception e) {
      logger.error(
          "Error getting Event Topic for topic id: " + config.getTopicId() + " " + e.getMessage());
      throw e;
    }
  }
}
