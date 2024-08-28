package bio.terra.common.events.topics;

import bio.terra.common.events.client.PubsubClientFactory;
import bio.terra.common.events.config.PubsubConfig;
import bio.terra.common.events.topics.messages.charts.ChartCreated;
import bio.terra.common.events.topics.messages.charts.ChartDeleted;
import bio.terra.common.events.topics.messages.charts.ChartMessage;
import bio.terra.common.events.topics.messages.charts.ChartUpdated;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class ChartTopic extends EventTopic<ChartMessage> {

  protected String publishedBy;

  protected ChartTopic(PubsubConfig config, PubsubClientFactory clientFactory) {
    super(clientFactory, "charts", config.publishedBy());
    publishedBy = config.publishedBy();
  }

  @NotNull
  private static String buildEntityUrl(String entityId) {
    return UriComponentsBuilder.newInstance()
        .path("api/admin/v1/charts")
        .queryParam("chartName", entityId)
        .toUriString();
  }

  public void chartCreated(String entityId) {
    publish(new ChartCreated(publishedBy, entityId, buildEntityUrl(entityId)));
  }

  public void chartUpdated(String entityId) {
    publish(new ChartUpdated(publishedBy, entityId, buildEntityUrl(entityId)));
  }

  public void chartDeleted(String entityId) {
    publish(new ChartDeleted(publishedBy, entityId, buildEntityUrl(entityId)));
  }
}
