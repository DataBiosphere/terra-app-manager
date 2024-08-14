package bio.terra.common.events.topics.messages;

public class ChartDeleted extends ChartMessage {
  public ChartDeleted(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, TYPES.CHART_DELETED, entityId, entityUrl);
  }
}
