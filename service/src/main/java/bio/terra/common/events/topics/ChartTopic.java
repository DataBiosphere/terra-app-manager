package bio.terra.common.events.topics;

import bio.terra.common.events.client.PubsubClient;
import bio.terra.common.events.config.PubsubConfig;
import bio.terra.common.events.topics.messages.ChartCreated;
import bio.terra.common.events.topics.messages.ChartDeleted;
import bio.terra.common.events.topics.messages.ChartMessage;
import bio.terra.common.events.topics.messages.ChartUpdated;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class ChartTopic extends EventTopic<ChartMessage> {

  private String publishedBy;

  public ChartTopic(PubsubConfig config, PubsubClient pubsubClient) {
    super(pubsubClient);
    publishedBy = config.publishedBy();
  }

  @Override
  protected Boolean process(ChartMessage message) {
    return Boolean.TRUE;
  }

  public void chartCreated(String entityId) {
    publish(new ChartCreated(publishedBy, entityId, buildEntityUrl(entityId)));
  }

  @NotNull
  private static String buildEntityUrl(String entityId) {
    return UriComponentsBuilder.newInstance()
        .path("api/admin/v1/charts")
        .queryParam("chartName", entityId)
        .toUriString();
  }

  public void chartUpdated(String entityId) {
    publish(new ChartUpdated(publishedBy, entityId, buildEntityUrl(entityId)));
  }

  public void chartDeleted(String entityId) {
    publish(new ChartDeleted(publishedBy, entityId, buildEntityUrl(entityId)));
  }
}
