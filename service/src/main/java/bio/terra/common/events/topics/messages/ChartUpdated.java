package bio.terra.common.events.topics.messages;

public class ChartUpdated extends ChartMessage {
  public ChartUpdated(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, TYPES.CHART_UPDATED, entityId, entityUrl);
  }
}
