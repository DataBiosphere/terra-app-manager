package bio.terra.common.events.client.google;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEventTopicIfNotExist extends EventTopicName {
  private static final Logger logger = LoggerFactory.getLogger(CreateEventTopicIfNotExist.class);
  private final String projectId;

  public CreateEventTopicIfNotExist(
      String projectId,
      boolean connectLocal,
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider) {
    super(connectLocal, channelProvider, credentialsProvider);
    this.projectId = projectId;
  }

  /**
   * This is called when running on a BEE Verify the topic exists or create the topic if it does not
   * exist Then return the TopicName
   *
   * @param name
   * @return TopicName for the Event topic for the environment
   */
  @Override
  public TopicName verifyTopicName(String name) throws IOException {
    try (TopicAdminClient topicAdminClient = buildTopicAdminClient()) {
      TopicName topicName = TopicName.of(projectId, name);

      try {
        topicAdminClient.getTopic(topicName);
      } catch (com.google.api.gax.rpc.NotFoundException e) {
        try {
          // topic not found, create it
          topicAdminClient.createTopic(topicName);
        } catch (PermissionDeniedException denied) {
          logger.error("Error creating BEE topic {0}", topicName, denied);
          throw denied;
        }
      }
      return topicName;
    }
  }
}
