package bio.terra.common.events.client;

import java.io.Closeable;

/**
 * The purpose of this class is to represent the client to the cloud-specific pubsub infrastructure.
 *
 * <p>To create an instance of this class, please see {@link PubsubClientFactory}
 */
public abstract class PubsubClient implements Closeable {

  public abstract void publish(byte[] message);

  public abstract void subscribe();
}
