package bio.terra.appmanager.dao;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.config.PublisherConfiguration;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

public class PublisherDaoTest extends BaseSpringBootTest {

  //  @Qualifier("mockPublisher")
  PublisherDao testPublisherDao =
      new PublisherDao(new PublisherConfiguration("topicId", "projectId"));

  @Mock Publisher publisher;

  @Test
  void testPublish() {
    ReflectionTestUtils.setField(testPublisherDao, "publisher", publisher);

    String msg = "test message";
    ByteString data = ByteString.copyFromUtf8(msg);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

    testPublisherDao.publish(msg);
    verify(publisher, times(1)).publish(pubsubMessage);
  }

  //  @TestConfiguration
  //  public static class MockPublisherConfiguration {
  //    @Bean(name = "mockPublisher")
  //    public PublisherDao getPublisherDao() {
  //      return new PublisherDao(new PublisherConfiguration("topicId", "projectId"));
  //    }
  //  }
}
