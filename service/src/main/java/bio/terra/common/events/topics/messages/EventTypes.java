package bio.terra.common.events.topics.messages;

/**
 * It is important to note that JAVA has a weird ordinal value for enums, so, order is usually important.
 *
 * That being said, we've really tried to only use the string representations of these enums. DO NOT USE THE ORDINAL VALUE of these enums.
 */
public enum EventTypes {
  CHART_CREATED,
  CHART_UPDATED,
  CHART_DELETED,


}
