package bio.terra.common.events.topics;

import bio.terra.common.events.client.MessageProcessor;
import bio.terra.common.events.client.PubsubClient;
import bio.terra.common.events.client.PubsubClientFactory;
import bio.terra.common.events.topics.messages.EventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class is responsible interacting with the PubsubClient (both publish and subscribe). */
public abstract class EventTopic<T extends EventMessage> {

  private static final Logger logger = LoggerFactory.getLogger(EventTopic.class);

  private PubsubClient client;

  protected EventTopic(PubsubClientFactory clientFactory, String topicName, String serviceName) {
    client = clientFactory.createPubsubClient(topicName, serviceName);
  }

  public void publish(T message) {
    try {
      client.publish(message.toJson());
    } catch (JsonProcessingException e) {
      logger.error("unable to publish event", e);
    }
  }

  public void subscribe() {
    subscribe(this::receive);
  }

  public void subscribe(MessageProcessor processor) {
    client.subscribe(processor);
  }

  protected boolean receive(String message) {
    try {
      T msg = (T) EventMessage.fromJson(message);
      return process(msg);
    } catch (JsonProcessingException e) {
      logger.error("Error while converting message data to EventMessage", e);
      // TODO: what to do with bad messages
    }

    return false;
  }

  /**
   * This is the method for how we respond to messages of a specific type via the PubSub
   * subscriptions for the specific subscriber.
   *
   * @param message
   * @return
   */
  public abstract boolean process(EventMessage message);
}
