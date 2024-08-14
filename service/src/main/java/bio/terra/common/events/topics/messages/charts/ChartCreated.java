package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;

public class ChartCreated extends ChartMessage {
  public ChartCreated(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, EventMessage.TYPES.CHART_CREATED, entityId, entityUrl);
  }
}
