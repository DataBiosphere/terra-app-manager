package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.List;

public abstract class ChartMessage extends EventMessage {

  private static List<EventTypes> CHART_TYPES =
      Arrays.asList(EventTypes.CHART_CREATED, EventTypes.CHART_UPDATED, EventTypes.CHART_DELETED);

  public ChartMessage(String publishedBy, EventTypes eventType, String entityId, String entityUrl) {
    super(publishedBy, eventType, entityId, entityUrl);
  }

  public ChartMessage(EventMessage event) {
    super(event);
    //    if (!CHART_TYPES.contains(event.eventType)) {
    //      throw new IllegalArgumentException(
    //          MessageFormat.format(
    //              "unexpected eventType({1}) while creating Chart event", event.eventType));
    //    }
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
        //      default:
        //        throw new IllegalStateException(
        //            MessageFormat.format("unexpected EventType while processing Chart events:
        // {1}", json));
    }
    return null;
  }
}
