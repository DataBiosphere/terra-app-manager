package bio.terra.appmanager.events;

import bio.terra.common.events.client.PubsubClientFactory;
import bio.terra.common.events.config.PubsubConfig;
import bio.terra.common.events.topics.ChartTopic;
import bio.terra.common.events.topics.messages.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ChartEvents extends ChartTopic {

  private static final Logger logger = LoggerFactory.getLogger(ChartEvents.class);

  public ChartEvents(PubsubConfig config, PubsubClientFactory factory) {
    super(config, factory);
    subscribe();
  }

  @Override
  public boolean process(EventMessage message) {
    logger.info("Received message: {}", message.eventType);
    return true;
  }
}
