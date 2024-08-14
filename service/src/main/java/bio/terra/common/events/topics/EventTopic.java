package bio.terra.common.events.topics;

import bio.terra.common.events.client.PubsubClient;
import bio.terra.common.events.topics.messages.EventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.charset.StandardCharsets;

/** This class is responsible interacting with the PubsubClient (both publish and subscribe). */
public abstract class EventTopic<T extends EventMessage> {

  private PubsubClient client;

  public EventTopic(PubsubClient client) {
    this.client = client;
  }

  public void publish(T message) {
    try {
      client.publish(message.toJson().getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException e) {
      System.out.println("ERROR: unable to publish message");
    }
  }

  public void subscribe() {
    T message = null;
    process(message);
  }

  /**
   * This is the method for how we respond to messages of a specific type via the PubSub
   * subscriptions for the specific subscriber.
   *
   * @param message
   * @return
   */
  protected abstract Boolean process(T message);
}
