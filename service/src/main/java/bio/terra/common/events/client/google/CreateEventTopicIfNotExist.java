package bio.terra.common.events.client.google;

import bio.terra.appmanager.config.ChartPublisherConfig;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEventTopicIfNotExist implements EventTopicName {
  private static final Logger logger = LoggerFactory.getLogger(CreateEventTopicIfNotExist.class);
  private final String projectId;

  public CreateEventTopicIfNotExist(String projectId) {
    this.projectId = projectId;
  }

  /**
   * This is called when running on a BEE Verify the topic exists or create the topic if it does not
   * exist Then return the TopicName
   *
   * @param config
   * @return TopicName for the Event topic for the environment
   */
  @Override
  public TopicName getEventTopicName(ChartPublisherConfig config) throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      TopicName topicName = TopicName.of(projectId, config.getTopicId());

      try {
        Topic topic = topicAdminClient.getTopic(topicName);
      } catch (com.google.api.gax.rpc.NotFoundException e) {
        // topic not found, create it
        try {
          Topic newTopic = topicAdminClient.createTopic(topicName);
        } catch (PermissionDeniedException denied) {
          logger.error("Error creating BEE topic " + topicName + " " + denied);
          // throw denied;
        }
      }
      return topicName;
    } catch (Exception e) {
      logger.error("Error getting topic: " + e.getMessage());
      throw e;
    }
  }
}
