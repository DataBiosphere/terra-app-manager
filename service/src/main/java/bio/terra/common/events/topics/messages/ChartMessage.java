package bio.terra.common.events.topics.messages;

import bio.terra.common.events.topics.EventMessage;

public abstract class ChartMessage extends EventMessage {

  public ChartMessage(String publishedBy, TYPES eventType, String entityId, String entityUrl) {
    super(publishedBy, eventType, entityId, entityUrl);
  }
}
