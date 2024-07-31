package bio.terra.common.events.client;

/**
 * The purpose of this class is to represent the client to the cloud-specific pubsub infrastructure.
 *
 * <p>To create an instance of this class, please see {@link PubsubClientFactory}
 */
public abstract class PubsubClient {

  public abstract void publish();

  public abstract void subscribe();
}
