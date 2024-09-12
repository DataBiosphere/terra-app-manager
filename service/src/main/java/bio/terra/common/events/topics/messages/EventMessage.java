package bio.terra.common.events.topics.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * @see <a
 *     href="https://broadworkbench.atlassian.net/wiki/spaces/IA/pages/3048931368/Terra+Event-driven+Architecture+WIP#Message-Schema-Definition">Terra
 *     Event-driven Architecture</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventMessage {

  /**
   * This is the current version of the EventMessage schema, as defined in the spec (see class-level
   * javadoc).
   *
   * <p>If we update this version, please be sure to make appropriate changes to schema in
   * Terraform.
   *
   * @see:
   */
  public static String MESSAGE_VERSION = "1.0.0";

  @JsonIgnore String id;

  @JsonProperty("version")
  String version;

  @JsonProperty("published_at")
  Date publishedAt;

  @JsonProperty("published_by")
  String publishedBy;

  @JsonProperty("context")
  Map<String, Object> context;

  @JsonProperty("job_id")
  String jobId;

  @JsonProperty("event_type")
  public EventTypes eventType;

  @JsonProperty("entity_id")
  String entityId;

  @JsonProperty("entity_url")
  String entityUrl;

  @JsonProperty("properties")
  Map<String, String> properties;

  /** This is needed for jackson deserialization with unknown attributes */
  private EventMessage() {}

  /**
   * This constructor is used to create an EventMessage of a specific type based on an actual
   * incoming message. It is also intentionally package private.
   *
   * <p>The following attributes are populated by other sources:
   *
   * <ul>
   *   <li>id: populated from pubsub message
   *   <li>version: static value at initial message creation
   *   <li>publishedAt: populated from pubsub message
   * </ul>
   *
   * @param id this comes from the underlying messaging framework
   * @param version the version of the event message
   * @param publishedAt date/time the message was published at. if not provided, then the value
   *     shall be taken from the message itself. (from message framework)
   * @param publishedBy the originating party for this message
   * @param context a map of
   * @param jobId if the event is a response to a particular async job, then this field shall hold
   *     the jobId to identify the async task
   * @param eventType string representation of the event type
   * @param entityId the id of the entity represented by this event
   * @param entityUrl service url location for the entity
   * @param properties collection of additional String-based name-value pairs - event specific
   */
  protected EventMessage(
      String id,
      String version,
      Date publishedAt,
      String publishedBy,
      Map<String, Object> context,
      String jobId,
      EventTypes eventType,
      String entityId,
      String entityUrl,
      Map<String, String> properties) {
    super();
    this.id = id;
    this.version = version;
    this.publishedAt = publishedAt;
    this.publishedBy = publishedBy;
    this.context = context;
    this.jobId = jobId;
    this.eventType = eventType;
    this.entityId = entityId;
    this.entityUrl = entityUrl;
    this.properties = properties;

    confirmNonNull();
  }

  public EventMessage(
      String publishedBy,
      Map<String, Object> context,
      String jobId,
      EventTypes eventType,
      String entityId,
      String entityUrl,
      Map<String, String> properties) {
    this(
        null,
        MESSAGE_VERSION,
        null,
        publishedBy,
        context,
        jobId,
        eventType,
        entityId,
        entityUrl,
        properties);
  }

  public EventMessage(String publishedBy, EventTypes eventType, String entityId, String entityUrl) {
    this(publishedBy, null, null, eventType, entityId, entityUrl, null);
  }

  protected EventMessage(EventMessage event) {
    this(
        event.id,
        event.version,
        event.publishedAt,
        event.publishedBy,
        event.context,
        event.jobId,
        event.eventType,
        event.entityId,
        event.entityUrl,
        event.properties);
  }

  public static EventMessage fromJson(String jsonMessage) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    EventMessage em = mapper.readValue(jsonMessage, EventMessage.class);
    return em;
  }

  public String toJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    var jsonMsg = mapper.writeValueAsString(this);
    return jsonMsg;
  }

  /**
   * Ensure required parameters are set on object.
   *
   * @return <tt>true</tt> if object is valid, <tt>false</tt> otherwise.
   * @see #EventMessage(String, String, Date, String, Map, String, EventTypes, String, String, Map)
   */
  private void confirmNonNull() {

    if (version == null
        || publishedBy == null
        || eventType == null
        || entityId == null
        || entityUrl == null) {
      throw new IllegalArgumentException(
          MessageFormat.format(
              "one of version({0}), publishedBy({1}), eventType({2}), entityId({3}), or entityUrl({4}) is missing",
              version, publishedBy, eventType, entityId, entityUrl));
    }
  }
}
