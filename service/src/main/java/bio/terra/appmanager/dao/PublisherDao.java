package bio.terra.appmanager.dao;

import bio.terra.appmanager.config.GooglePublisherConfiguration;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PublisherDao implements Closeable {
  private static final Logger logger = LoggerFactory.getLogger(PublisherDao.class);

  private final Publisher publisher;
  // The config is not actually used in the DAO of the time of writing, but it is included here with
  // a getter such that it can be inspected after the `Publisher` construction
  private final GooglePublisherConfiguration config;

  public PublisherDao(Publisher publisher, GooglePublisherConfiguration config) {
    this.publisher = publisher;
    this.config = config;
  }

  // TODO: use proper message typing here, depends on
  // https://broadworkbench.atlassian.net/browse/IA-5018 and
  // https://broadworkbench.atlassian.net/browse/IA-5019
  public void publish(String message) {
    ByteString data = ByteString.copyFromUtf8(message);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
    ApiFuture<String> future = publisher.publish(pubsubMessage);

    ApiFutures.addCallback(future, makePublishCallback(message), MoreExecutors.directExecutor());
  }

  private ApiFutureCallback<String> makePublishCallback(String message) {
    return new ApiFutureCallback<>() {
      @Override
      public void onFailure(Throwable throwable) {
        logger.error("Error publishing message : " + message, throwable);
      }

      @Override
      public void onSuccess(String messageId) {
        // Once published, returns server-assigned message ids (unique within the topic)
        logger.info("Published message ID: " + messageId);
      }
    };
  }

  @Override
  public void close() {
    if (publisher != null) {
      publisher.shutdown();
      try {
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public GooglePublisherConfiguration getConfig() {
    return config;
  }
}
