package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;
import bio.terra.common.events.topics.messages.EventTypes;

public class ChartUpdated extends ChartMessage {
  public ChartUpdated(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, EventTypes.CHART_UPDATED, entityId, entityUrl);
  }

  public ChartUpdated(EventMessage event) {
    super(event);
  }
}
