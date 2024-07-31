package bio.terra.appmanager.events;

import bio.terra.common.events.client.PubsubClient;
import bio.terra.common.events.topics.ChartTopic;
import org.springframework.stereotype.Repository;

@Repository
public class ChartEvents extends ChartTopic {
  public ChartEvents(PubsubClient client) {
    super(client);
  }
}
