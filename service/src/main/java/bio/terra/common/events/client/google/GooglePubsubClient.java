package bio.terra.common.events.client.google;

import bio.terra.common.events.client.PubsubClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for interacting with the Google PubSub infrastructure and handling both
 * publishing to topics and subscribing from topics.
 */
public class GooglePubsubClient extends PubsubClient {

  private static final Logger logger = LoggerFactory.getLogger(GooglePubsubClient.class);

  @Override
  public void publish(byte[] message) {
    if (logger.isDebugEnabled()) {
      logger.debug(new String(message, StandardCharsets.UTF_8));
    }
    // TODO: remove me
    logger.info(new String(message, StandardCharsets.UTF_8));
  }

  @Override
  public void subscribe() {}

  @Override
  public void close() throws IOException {}
}
