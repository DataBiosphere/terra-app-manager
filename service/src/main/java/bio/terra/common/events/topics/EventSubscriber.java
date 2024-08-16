package bio.terra.common.events.topics;

import bio.terra.common.events.topics.messages.EventMessage;

@FunctionalInterface
public interface EventSubscriber<T extends EventMessage> {
  boolean processEvent(String jsonString);
}
