package bio.terra.appmanager.events;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.common.events.topics.EventTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
/**
 * https://java.testcontainers.org https://java.testcontainers.org/modules/gcloud/#pubsub
 * https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
 * https://cloud.google.com/pubsub/docs/emulator
 */
public abstract class BaseEventsTest<T extends EventTopic<?>> extends BaseSpringBootTest {

  @TestConfiguration
  public static class MockEventConfiguration<T> {
    @Bean(name = "localEvents")
    public T getEventsMock(ChartDao chartDao, ChartEvents chartPublisher) {
      return null;
    }
  }
}
