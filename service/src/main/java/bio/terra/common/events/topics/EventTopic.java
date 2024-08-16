package bio.terra.common.events.topics;

import bio.terra.common.events.client.MessageProcessor;
import bio.terra.common.events.client.PubsubClient;
import bio.terra.common.events.client.PubsubClientFactory;
import bio.terra.common.events.topics.messages.EventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

/** This class is responsible interacting with the PubsubClient (both publish and subscribe). */
public abstract class EventTopic<T extends EventMessage> {

  private PubsubClient client;

  public EventTopic(PubsubClientFactory clientFactory, String topicName, String serviceName) {
    client = clientFactory.createPubsubClient(topicName, serviceName, this::receive);
  }

  public void publish(T message) {
    try {
      client.publish(message.toJson());
    } catch (JsonProcessingException e) {
      System.out.println("ERROR: unable to publish message");
    }
  }

  public void subscribe(MessageProcessor processor) {
    client.subscribe(processor);
  }

  protected boolean receive(String message) {
    try {
      T msg = (T) EventMessage.fromJson(message);
      return process(msg);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
      // TODO: what to do with bad messages
    }

    // TODO: this needs to be changed back to false
    return true;
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
