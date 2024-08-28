package bio.terra.appmanager.events;

import bio.terra.common.events.topics.messages.charts.ChartCreated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChartEventsTest extends BaseEventsTest {

  @Autowired ChartEvents events;

  @Test
  public void testPubsub() {
    System.out.println("PUBSUB_CONTAINER.getEmulatorEndpoint()");
    System.out.println(PUBSUB_CONTAINER.getEmulatorEndpoint());

    events.publish(new ChartCreated("chart-events-test", "some-entity-id", "some-entity-url"));
  }
}
