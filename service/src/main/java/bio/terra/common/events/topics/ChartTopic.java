package bio.terra.common.events.topics;

import bio.terra.common.events.client.PubsubClient;
import org.springframework.stereotype.Repository;

@Repository
public class ChartTopic extends AbstractEventTopic<ChartMessage> {

  public ChartTopic(PubsubClient pubsubClient) {
    super(pubsubClient);
  }

  @Override
  protected Boolean process(ChartMessage message) {
    return Boolean.TRUE;
  }
}
