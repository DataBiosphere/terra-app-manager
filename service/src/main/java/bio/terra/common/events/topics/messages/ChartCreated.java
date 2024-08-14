package bio.terra.common.events.topics.messages;

public class ChartCreated extends ChartMessage {
  public ChartCreated(String publishedBy, String entityId, String entityUrl) {
    super(publishedBy, TYPES.CHART_CREATED, entityId, entityUrl);
  }
}
