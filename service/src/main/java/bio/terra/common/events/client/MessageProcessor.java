package bio.terra.common.events.client;

@FunctionalInterface
public interface MessageProcessor {
  boolean process(String jsonString);
}
