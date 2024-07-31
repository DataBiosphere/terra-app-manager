package bio.terra.common.events.client;

import bio.terra.common.events.client.google.GooglePubsubClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Based on the various configs that are out there for pubsub, create an instance of the pubsub
 * client to be used by a Spring Boot application.
 */
@Component
public class PubsubClientFactory {

  @Bean(name = "pubsubClient")
  public PubsubClient createPubsubClient() {
    return new GooglePubsubClient();
  }
}
