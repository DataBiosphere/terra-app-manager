package bio.terra.appmanager.events;

import bio.terra.common.events.client.PubsubClientFactory;
import bio.terra.common.events.config.PubsubConfig;
import bio.terra.common.events.topics.ChartTopic;
import bio.terra.common.events.topics.messages.EventMessage;
import org.springframework.stereotype.Repository;

@Repository
public class ChartEvents extends ChartTopic {
  public ChartEvents(PubsubConfig config, PubsubClientFactory factory) {
    super(config, factory);
    // TODO: default to this::receive if you don't provide one
    subscribe(this::receive);
  }

  @Override
  public boolean process(EventMessage message) {
    System.out.println("Received message: " + message.eventType);
    return true;
  }
}
