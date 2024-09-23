package bio.terra.common.events.client.google;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.naming.ConfigurationException;

public class EventTopicMustBeAlreadyCreated extends EventTopicName {
  private final String projectId;

  public EventTopicMustBeAlreadyCreated(
      String projectId,
      boolean connectLocal,
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider) {
    super(connectLocal, channelProvider, credentialsProvider);
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
    }
  }
}
