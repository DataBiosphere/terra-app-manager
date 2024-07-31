package bio.terra.common.events.topics;

import bio.terra.common.events.client.PubsubClient;

/** This class is responsible interacting with the PubsubClient (both publish and subscribe). */
public abstract class EventClient<T extends EventMessage> {

  private PubsubClient client;

  public EventClient(PubsubClient client) {
    this.client = client;
  }

  public void publish(T message) {
    System.out.println("Hello: " + message);
  }

  public void subscribe() {
    T message = null;
    process(message);
  }

  protected abstract Boolean process(T message);
}
