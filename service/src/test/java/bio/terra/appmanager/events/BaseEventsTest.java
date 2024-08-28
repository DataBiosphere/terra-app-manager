package bio.terra.appmanager.events;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.common.events.topics.EventTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

@Transactional
@Rollback
/**
 * https://java.testcontainers.org https://java.testcontainers.org/modules/gcloud/#pubsub
 * https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
 * https://cloud.google.com/pubsub/docs/emulator
 */
public abstract class BaseEventsTest<T extends EventTopic<?>> extends BaseSpringBootTest {
  protected static final PubSubEmulatorContainer PUBSUB_CONTAINER;

  static {
    PUBSUB_CONTAINER =
        new PubSubEmulatorContainer(
            DockerImageName.parse(
                "gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"));
    PUBSUB_CONTAINER.start();
  }

  @TestConfiguration
  public static class MockEventConfiguration<T> {
    @Bean(name = "localEvents")
    public T getEventsMock(ChartDao chartDao, ChartEvents chartPublisher) {
      return null;
    }
  }

  //  public String getXxx() {
  //    ManagedChannel channel =
  //        ManagedChannelBuilder.forTarget(PUBSUB_CONTAINER.getEmulatorEndpoint())
  //            .usePlaintext()
  //            .build();
  //    TransportChannelProvider channelProvider =
  //        FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
  //    CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
  //  }

  //  set up PUBSUB_EMULATOR_HOST
  //  String hostport = System.getenv("PUBSUB_EMULATOR_HOST");
  //
  //    // Set the channel and credentials provider when creating a `TopicAdminClient`.
  //    // Similarly for SubscriptionAdminClient
  //    TopicAdminClient topicClient =
  //        TopicAdminClient.create(
  //            TopicAdminSettings.newBuilder()
  //                .setTransportChannelProvider(channelProvider)
  //                .setCredentialsProvider(credentialsProvider)
  //                .build());
  //
  //    TopicName topicName = TopicName.of("my-project-id", "my-topic-id");
  //    // Set the channel and credentials provider when creating a `Publisher`.
  //    // Similarly for Subscriber
  //    Publisher publisher =
  //        Publisher.newBuilder(topicName)
  //            .setChannelProvider(channelProvider)
  //            .setCredentialsProvider(credentialsProvider)
  //            .build();
  //  } finally {
  //    channel.shutdown();
  //  }

}
