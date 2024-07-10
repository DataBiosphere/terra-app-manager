package bio.terra.appmanager.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.config.PublisherConfiguration;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PublisherDaoTest extends BaseSpringBootTest {

  PublisherDao testPublisherDao =
      new PublisherDao(new PublisherConfiguration("topicId", "projectId"));

  @Mock Publisher publisher;

  @Test
  void testPublish() {
    ReflectionTestUtils.setField(testPublisherDao, "publisher", publisher);

    String msg = "test message";
    ByteString data = ByteString.copyFromUtf8(msg);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

    ApiFuture<String> publishReturn = Mockito.mock(ApiFuture.class, Answers.CALLS_REAL_METHODS);
    when(publisher.publish(any())).thenReturn(publishReturn);
    testPublisherDao.publish(msg);
    verify(publisher, times(1)).publish(pubsubMessage);
  }

  @Test
  void testClose() throws InterruptedException {
    ReflectionTestUtils.setField(testPublisherDao, "publisher", publisher);

    testPublisherDao.close();
    verify(publisher, times(1)).shutdown();
    verify(publisher, times(1)).awaitTermination(1, TimeUnit.MINUTES);
  }
}
