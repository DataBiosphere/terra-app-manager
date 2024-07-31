package bio.terra.common.events.client.google;

public class TopicCreatorFactory {

  public static EventTopicName createCreateEventTopicIfNotExist(String projectId) {
    return new CreateEventTopicIfNotExist(projectId);
  }

  public static EventTopicName createEventTopicMustBeAlreadyCreated(String projectId) {
    return new EventTopicMustBeAlreadyCreated(projectId);
  }
}
