package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;
import bio.terra.common.events.topics.messages.EventTypes;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class ChartMessage extends EventMessage {

  protected ChartMessage(
      String publishedBy, EventTypes eventType, String entityId, String entityUrl) {
    super(publishedBy, eventType, entityId, entityUrl);
  }

  protected ChartMessage(EventMessage event) {
    super(event);
  }

  public static ChartMessage fromJson(String json) throws JsonProcessingException {
    EventMessage event = EventMessage.fromJson(json);
    switch (event.eventType) {
      case CHART_CREATED:
        return new ChartCreated(event);
      case CHART_UPDATED:
        return new ChartUpdated(event);
      case CHART_DELETED:
        return new ChartDeleted(event);
      default:
        throw new IllegalStateException(
            "unexpected EventType while processing Chart events:" + json);
    }
  }
}
