package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;

public class ChartDeleted extends ChartMessage {
  public ChartDeleted(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, EventMessage.TYPES.CHART_DELETED, entityId, entityUrl);
  }
}
