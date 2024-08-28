package bio.terra.common.events.client;

import bio.terra.common.events.client.google.GooglePubsubClient;
import bio.terra.common.events.config.PubsubConfig;
import bio.terra.common.events.topics.EventSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Based on the various configs that are out there for pubsub, create an instance of the pubsub
 * client to be used by a Spring Boot application.
 *
 * <p>Current clients supported are:
 *
 * <ul>
 *   <li>GoogleClient
 * </ul>
 *
 * <p>This class would need to be added to if additional clients become supported (like Azure, AWS,
 * etc.)
 */
@Component
public class PubsubClientFactory {

  private PubsubConfig pubsubConfig;

  public PubsubClientFactory(PubsubConfig config) {
    this.pubsubConfig = config;
  }

  public PubsubClient createPubsubClient(
      String topicName, String serviceName, EventSubscriber subscriber) {
    return new GooglePubsubClient(
        pubsubConfig.googleConfig().projectId(),
        formatTopicId(topicName),
        formatSubscriptionId(serviceName, topicName),
        pubsubConfig.createTopic(),
        pubsubConfig.connectLocal(),
        pubsubConfig.emulatorTarget());
  }

  private String formatTopicId(String topicName) {
    List<String> parts = new ArrayList<>(Arrays.asList("event", topicName));

    if (pubsubConfig.nameSuffix() != null) {
      parts.add(pubsubConfig.nameSuffix());
    }

    return String.join("-", parts);
  }

  private String formatSubscriptionId(String serviceName, String topicName) {
    if (serviceName == null) {
      return null;
    }

    List<String> parts = new ArrayList<>(Arrays.asList("subscription", serviceName, topicName));

    return String.join("-", parts);
  }
}
