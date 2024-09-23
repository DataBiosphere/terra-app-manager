package bio.terra.common.events.topics.messages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class PubsubEmulator {
  protected static final PubSubEmulatorContainer PUBSUB_CONTAINER;

  static {
    PUBSUB_CONTAINER =
        new PubSubEmulatorContainer(
            DockerImageName.parse(
                "gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"));
    PUBSUB_CONTAINER.start();
  }

  @Bean(name = "pubsubEmulatorEndpoint")
  public String pubsubEmulatorEndpoint() {
    return PUBSUB_CONTAINER.getEmulatorEndpoint();
  }
}
