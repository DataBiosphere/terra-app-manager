package bio.terra.common.events.client;

import java.io.Closeable;

/**
 * The purpose of this class is to represent the client to the cloud-specific pubsub infrastructure
 * for a single topic.
 *
 * <p>To create an instance of this class, please see {@link PubsubClientFactory}
 *
 * <p>The PubsubClient is responsible for ensuring the following conditions:
 *
 * <ul>
 *   <li>the topic exists
 * </ul>
 */
public abstract interface PubsubClient extends Closeable {

  public abstract void publish(String message);

  public abstract void subscribe(MessageProcessor process);
}
