package bio.terra.common.events.topics.messages.charts;

import bio.terra.common.events.topics.messages.EventMessage;

public abstract class ChartMessage extends EventMessage {

  public ChartMessage(String publishedBy, TYPES eventType, String entityId, String entityUrl) {
    super(publishedBy, eventType, entityId, entityUrl);
  }
}
