package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;
import bio.terra.common.events.topics.messages.EventTypes;

public class ChartDeleted extends ChartMessage {
  public ChartDeleted(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, EventTypes.CHART_DELETED, entityId, entityUrl);
  }

  public ChartDeleted(EventMessage event) {
    super(event);
  }
}
