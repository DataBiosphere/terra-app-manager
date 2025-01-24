package bio.terra.common.events.topics.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Confirms serialization (and deserialization) is working as expected along with various mapping
 * capabilities for EventMessage(s).
 */
public class EventMessageTest {

  /**
   * @throws Exception
   */
  @Test
  void verifyToJson() throws Exception {
    EventMessage em =
        new EventMessage("publisher", EventTypes.CHART_CREATED, "entity-id", "entity-url");

    ObjectMapper mapper = new ObjectMapper();
    validateRequiredAttributes(em, mapper.readTree(em.toJson()));
  }

  @Test
  void verifyFromJson() throws Exception {
    String jsonMessage =
        "{\"version\":\"1.0.0\",\"published_by\":\"publisher\",\"event_type\":\"CHART_CREATED\",\"entity_id\":\"entity-id\",\"entity_url\":\"entity-url\"}\n";

    ObjectMapper mapper = new ObjectMapper();
    validateRequiredAttributes(EventMessage.fromJson(jsonMessage), mapper.readTree(jsonMessage));
  }

  @Test
  void requiredParams_publishedBy() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new EventMessage(null, EventTypes.CHART_CREATED, "entity-id", "entity-url");
            });
    assertTrue(exception.getMessage().contains("publishedBy(null)"));
  }

  @Test
  void requiredParams_eventType() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new EventMessage("publisher", null, "entity-id", "entity-url");
            });
    assertTrue(exception.getMessage().contains("eventType(null)"));
  }

  @Test
  void requiredParams_entityId() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new EventMessage("publisher", EventTypes.CHART_CREATED, null, "entity-url");
            });
    assertTrue(exception.getMessage().contains("entityId(null)"));
  }

  @Test
  void requiredParams_entityUrl() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new EventMessage("publisher", EventTypes.CHART_CREATED, "entity-id", null);
            });
    assertTrue(exception.getMessage().contains("entityUrl(null)"));
  }

  private static void validateRequiredAttributes(EventMessage em, JsonNode node) {
    assertEquals(em.version, node.get("version").textValue());
    assertEquals(em.publishedBy, node.get("published_by").textValue());
    assertEquals(em.eventType.name(), node.get("event_type").textValue());
    assertEquals(em.entityId, node.get("entity_id").textValue());
    assertEquals(em.entityUrl, node.get("entity_url").textValue());
  }
}
