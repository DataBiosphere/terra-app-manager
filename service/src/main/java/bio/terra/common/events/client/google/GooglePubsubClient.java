package bio.terra.common.events.client.google;

import bio.terra.common.events.client.MessageProcessor;
import bio.terra.common.events.client.PubsubClient;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.naming.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for interacting with the Google PubSub infrastructure and handling both
 * publishing to topics and subscribing from topics.
 */
public class GooglePubsubClient implements PubsubClient {

  private static final Logger logger = LoggerFactory.getLogger(GooglePubsubClient.class);

  private String projectId;
  private String topicId;
  private String subscriptionId;
  private Publisher publisher;
  private Subscriber subscriber;

  private boolean connectLocal;
  private TransportChannelProvider channelProvider;
  private CredentialsProvider credentialsProvider;

  public GooglePubsubClient(
      String projectId,
      String topicId,
      String subscriptionId,
      boolean createTopic,
      boolean connectLocal,
      String emulatorTarget) {
    this.projectId = projectId;
    this.topicId = topicId;
    this.subscriptionId = subscriptionId;

    this.connectLocal = connectLocal;
    if (connectLocal) {
      ManagedChannel channel =
          ManagedChannelBuilder.forTarget(emulatorTarget).usePlaintext().build();
      channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
      credentialsProvider = NoCredentialsProvider.create();
    }

    publisher =
        buildPublisher(
            projectId, topicId, createTopic, connectLocal, channelProvider, credentialsProvider);
  }

  @Override
  public void publish(String message) {
    if (logger.isDebugEnabled()) {
      logger.debug(message);
    }
    ByteString data = ByteString.copyFromUtf8(message);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

    ApiFuture<String> future = publisher.publish(pubsubMessage);
    ApiFutures.addCallback(future, makePublishCallback(message), MoreExecutors.directExecutor());
  }

  @Override
  public void subscribe(MessageProcessor processor) {
    ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(projectId, subscriptionId);

    // Instantiate an asynchronous message receiver.
    MessageReceiver receiver =
        (PubsubMessage message, AckReplyConsumer consumer) -> {
          // Handle incoming message, then ack the received message.
          String eventMsg = message.getData().toStringUtf8();
          if (logger.isDebugEnabled()) {
            logger.debug("Received: id: {} data: {}", message.getMessageId(), eventMsg);
          }
          if (processor.process(eventMsg)) {
            consumer.ack();
          } else {
            logger.warn("Failed: id: {}", message.getMessageId());
            consumer.nack();
          }
        };

    Subscriber.Builder builder = Subscriber.newBuilder(subscriptionName, receiver);
    if (connectLocal) {
      builder.setChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider);
    }
    subscriber = builder.build();

    // Start the subscriber.
    subscriber.startAsync().awaitRunning();
    logger.info("Listening for messages on {}", subscriptionName);
  }

  @Override
  public void close() throws IOException {
    closePublisher();
    closeSubscriber();
  }

  private Publisher buildPublisher(
      String projectId,
      String topicName,
      boolean createTopic,
      boolean connectLocal,
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider) {
    try {
      logger.info("Building events publisher: {}:{}", projectId, topicName);
      TopicName topic =
          verifyTopic(
              projectId,
              topicName,
              createTopic,
              connectLocal,
              channelProvider,
              credentialsProvider);
      Publisher.Builder builder = Publisher.newBuilder(topic);
      if (connectLocal) {
        builder.setChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider);
      }
      return builder.build();
    } catch (IOException | ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private void closePublisher() {
    if (publisher != null) {
      logger.info("Stopping events publisher: {}:{}", projectId, topicId);
      publisher.shutdown();
      try {
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }

  private void closeSubscriber() {
    if (subscriber != null) {
      // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
      subscriber.stopAsync();
      try {
        subscriber.awaitTerminated(1, TimeUnit.MINUTES);
      } catch (TimeoutException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private TopicName verifyTopic(
      String projectId,
      String topicName,
      boolean createTopic,
      boolean connectLocal,
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider)
      throws IOException, ConfigurationException {
    EventTopicName topicCreator = null;
    if (createTopic) {
      topicCreator =
          new CreateEventTopicIfNotExist(
              projectId, connectLocal, channelProvider, credentialsProvider);
    } else {
      topicCreator =
          new EventTopicMustBeAlreadyCreated(
              projectId, connectLocal, channelProvider, credentialsProvider);
    }
    return topicCreator.verifyTopicName(topicName);
  }

  private ApiFutureCallback<String> makePublishCallback(String message) {
    return new ApiFutureCallback<>() {
      @Override
      public void onFailure(Throwable throwable) {
        logger.error("Error publishing message : {}", message, throwable);
      }

      @Override
      public void onSuccess(String messageId) {
        // Once published, returns server-assigned message ids (unique within the topic)
        logger.info("Published message ID: {}", messageId);
      }
    };
  }
}
