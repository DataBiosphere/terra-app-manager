package bio.terra.common.events.topics;

@FunctionalInterface
public interface EventSubscriber {
  boolean processEvent(String jsonString);
}
