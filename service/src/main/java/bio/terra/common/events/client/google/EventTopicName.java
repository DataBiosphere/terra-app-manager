package bio.terra.common.events.client.google;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import javax.naming.ConfigurationException;

public abstract class EventTopicName {

  private final boolean connectLocal;
  private final String emulatorTarget;

  public EventTopicName(boolean connectLocal, String emulatorTargetUrl) {
    this.connectLocal = connectLocal;
    // this is optional, and only required if connectLocal is false
    this.emulatorTarget = emulatorTargetUrl;
  }

  abstract TopicName verifyTopicName(String name) throws IOException, ConfigurationException;

  /**
   * Need to support both the standard building of the client,<br>
   * and the building of a client that connects to the Pubsub Emulator.
   *
   * <p>By default, the Emulator is used for:
   *
   * <ul>
   *   <li>local development (started manually)<br>
   *       local development is established through setting up through shell scripts and exposing
   *       the associated emulator environment variables.
   *   <li>testing environment (started by testing infrastructure)<br>
   *       testing infrastructure is established through the override of the configuration settings
   *       and extending the BaseEventsTest class
   * </ul>
   *
   * @return
   * @throws IOException
   */
  public TopicAdminClient buildTopicAdminClient() throws IOException {

    if (connectLocal) {
      ManagedChannel channel =
          ManagedChannelBuilder.forTarget(emulatorTarget).usePlaintext().build();
      TransportChannelProvider channelProvider =
          FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
      CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

      return TopicAdminClient.create(
          TopicAdminSettings.newBuilder()
              .setTransportChannelProvider(channelProvider)
              .setCredentialsProvider(credentialsProvider)
              .build());
    }

    return TopicAdminClient.create();
  }
}
